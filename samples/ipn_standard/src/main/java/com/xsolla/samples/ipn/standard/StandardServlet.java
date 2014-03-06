package com.xsolla.samples.ipn.standard;

import com.xsolla.samples.ipn.standard.storage.JdbcPaymentStandardStorage;
import com.xsolla.samples.ipn.standard.storage.JdbcUserStorage;
import com.xsolla.sdk.Project;
import com.xsolla.sdk.protocol.ProtocolFactory;
import com.xsolla.sdk.protocol.StandardProtocol;
import com.xsolla.sdk.protocol.http.ServletHttpAdapter;
import com.xsolla.sdk.protocol.request.Request;
import com.xsolla.sdk.protocol.response.Response;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;

public class StandardServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        super.doPost(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        final int PROJECT_ID = 4783;
        final String PROJECT_KEY = "key";

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://DB_HOST/DB_NAME", "DB_USER", "DB_PASSWORD");

            ServletHttpAdapter servletHttpAdapter = new ServletHttpAdapter(request, response);

            Request xsollaRequest = servletHttpAdapter.getRequest();
            Project project = new Project(PROJECT_ID, PROJECT_KEY);

            JdbcUserStorage userStorage = new JdbcUserStorage(connection);
            JdbcPaymentStandardStorage paymentStorage = new JdbcPaymentStandardStorage(connection);

            ProtocolFactory protocolFactory = new ProtocolFactory(project);
            StandardProtocol standardProtocol = protocolFactory.getStandardProtocol(userStorage, paymentStorage);
            Response xsollaResponse = standardProtocol.run(xsollaRequest);

            servletHttpAdapter.sendResponse(xsollaResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
