package com.xsolla.sdk.protocol.command;

import com.xsolla.sdk.Project;
import com.xsolla.sdk.exception.InvalidRequestException;
import com.xsolla.sdk.exception.InvalidSignException;
import com.xsolla.sdk.protocol.command.response.CommandResponse;
import com.xsolla.sdk.protocol.request.Request;
import org.apache.commons.lang3.StringUtils;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public abstract class Command {

    protected Project project;

    public CommandResponse getResponse(Request request) throws Exception {
        ArrayList missedParams = this.getMissedParams(request);
        if (!missedParams.isEmpty()) {
            StringBuilder exceptionMessage = new StringBuilder("Invalid request format. Missed parameters: ");
            exceptionMessage.append(StringUtils.join(missedParams, ", "));
            throw new InvalidRequestException(exceptionMessage.toString());
        }
        if (!this.checkSign(request)) {
            throw new InvalidSignException("Invalid md5 signature");
        }
        return this.process(request);
    }

    public ArrayList<String> getMissedParams(Request request) {
        ArrayList<String> missedParams = new ArrayList<>();
        List<String> requiredParams = this.getRequiredParams();
        for (String requiredParam : requiredParams) {
            if (!request.hasParam(requiredParam) || request.getParam(requiredParam).isEmpty()) {
                missedParams.add(requiredParam);
            }
        }
        return missedParams;
    }

    public String generateSign(Request request, List<String> params) throws NoSuchAlgorithmException {
        StringBuilder signStringBuilder = new StringBuilder("");
        for (String param : params) {
            signStringBuilder.append(request.getParam(param));
        }
        signStringBuilder.append(this.project.getSecretKey());
        byte[] sign = MessageDigest.getInstance("MD5")
                .digest(signStringBuilder.toString().getBytes());
        return String.format("%032x",new BigInteger(1, sign));
    }

    protected Date getDateXsolla(String format, String dateSting) {
        TimeZone timeZone = TimeZone.getTimeZone("Europe/Moscow");
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        dateFormat.setTimeZone(timeZone);
        try {
            return dateFormat.parse(dateSting);
        } catch (ParseException e) {
            String exceptionMessage = String.format("Date string '%s' does not match format '%s'", dateSting, format);
            throw new InvalidRequestException(exceptionMessage);
        }
    }

    protected String emptyStringToNull(String source) {
        if (source == null) {
            return null;
        }
        String trimmedString = source.trim();
        return trimmedString.isEmpty() ? null : trimmedString;
    }

    protected boolean isDryRun(Request request) {
        return request.hasParam("dryRun") && request.getParam("dryRun").equals("1");
    }

    abstract public boolean checkSign(Request request) throws NoSuchAlgorithmException;

    abstract public List<String> getRequiredParams();

    abstract protected CommandResponse process(Request request) throws Exception;

    abstract public CommandResponse getEmptyResponse();

}

