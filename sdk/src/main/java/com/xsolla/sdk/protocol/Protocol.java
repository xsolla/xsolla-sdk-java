package com.xsolla.sdk.protocol;

import com.xsolla.sdk.Project;
import com.xsolla.sdk.exception.InvalidRequestException;
import com.xsolla.sdk.exception.InvalidSignException;
import com.xsolla.sdk.exception.UnprocessableRequestException;
import com.xsolla.sdk.exception.WrongCommandException;
import com.xsolla.sdk.protocol.command.Command;
import com.xsolla.sdk.protocol.command.response.CommandResponse;
import com.xsolla.sdk.protocol.response.Response;
import com.xsolla.sdk.protocol.storage.IPaymentStorage;
import com.xsolla.sdk.protocol.request.Request;
import com.xsolla.sdk.validator.IpChecker;
import org.apache.commons.lang3.StringUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.List;

abstract public class Protocol {

    protected IPaymentStorage payments;
    protected Command currentCommand;
    protected CommandResponse response;
    protected Project project;
    protected IpChecker ipChecker;

    protected Protocol(Project project, IpChecker ipChecker) {
        this.project = project;
        this.ipChecker = ipChecker;
    }

    public Project getProject() {
        return project;
    }

    /**
     * Handle IPN request. Use IHttpAdapter for integrate to your WEB server software.
     * @param request
     * @return Response
     */
    public Response run(Request request) {
        CommandResponse commandResponse;
        try {
            commandResponse = this.doRun(request);
        } catch (UnprocessableRequestException e) {
            commandResponse = this.getUnprocessableRequestCommandResponse();
            commandResponse.setComment(e.getMessage());
        } catch (InvalidRequestException e) {
            commandResponse = this.currentCommand.getEmptyResponse();
            commandResponse.setResultAsInvalidRequest();
            commandResponse.setComment(e.getMessage());
        } catch (WrongCommandException e) {
            commandResponse = this.getProtocolEmptyCommandResponse();
            commandResponse.setResultAsInvalidRequest();
            commandResponse.setComment(e.getMessage());
        } catch (InvalidSignException e) {
            commandResponse = this.currentCommand.getEmptyResponse();
            commandResponse.setResultAsInvalidSign();
            commandResponse.setComment(e.getMessage());
        } catch (Exception e) {
            commandResponse = this.currentCommand.getEmptyResponse();
            commandResponse.setResultAsTemporaryServerError();
            String exceptionMessage = e.getMessage();
            if (exceptionMessage == null || exceptionMessage.isEmpty()) {
                StringWriter stringWriter = new StringWriter();
                e.printStackTrace(new PrintWriter(stringWriter));
                exceptionMessage = stringWriter.toString();
            }
            commandResponse.setComment(exceptionMessage);
        }
        Response response = new Response();
        response.setContent(commandResponse.toXml());
        return response;
    }

    protected CommandResponse doRun(Request request) throws Exception {
        if (this.ipChecker != null) {
            this.ipChecker.checkIp(request.getClientIP());
        }
        if (!request.hasParam("command")) {
            throw new WrongCommandException(String.format(
                    "No command in request. Available commands are: '%s'.",
                    StringUtils.join(this.getProtocolCommands(), "', '")
            ));
        }
        return doCommand(request);
    }

    protected CommandResponse getUnprocessableRequestCommandResponse() {
        CommandResponse unprocessableResponse;
        if (this.currentCommand != null) {
            unprocessableResponse = this.currentCommand.getEmptyResponse();
        } else {
            unprocessableResponse = this.getProtocolEmptyCommandResponse();
        }
        unprocessableResponse.setResultAsUnprocessableRequest();
        return unprocessableResponse;
    }

    public abstract IPaymentStorage getPaymentStorage();

    protected abstract CommandResponse getProtocolEmptyCommandResponse();

    protected abstract CommandResponse doCommand(Request request) throws Exception;

    protected abstract List<String> getProtocolCommands();

}
