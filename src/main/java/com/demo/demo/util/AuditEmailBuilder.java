package com.demo.demo.util;

import com.demo.demo.dto.response.DailyAuditReportDTO;
import com.demo.demo.dto.response.TransactionAuditDTO;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class AuditEmailBuilder {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd MMM yyyy");

    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss");

    public String buildDailyAuditEmail(
            DailyAuditReportDTO report
    ) {

        StringBuilder html = new StringBuilder();

        html.append("""
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body{
                            font-family:Arial,sans-serif;
                            background:#f4f4f4;
                            padding:20px;
                        }

                        .container{
                            background:#ffffff;
                            border-radius:8px;
                            padding:20px;
                        }

                        h2{
                            color:#0d6efd;
                        }

                        table{
                            width:100%;
                            border-collapse:collapse;
                            margin-top:15px;
                        }

                        th,td{
                            border:1px solid #dddddd;
                            padding:8px;
                            text-align:left;
                        }

                        th{
                            background:#0d6efd;
                            color:white;
                        }

                        .summary{
                            margin-top:20px;
                            margin-bottom:20px;
                        }

                        .summary p{
                            margin:5px 0;
                        }

                    </style>
                </head>
                <body>

                <div class="container">

                <h2>Daily Banking Audit Report</h2>
                """);

        html.append("<p><strong>From:</strong> ")
                .append(report.getFromDate().format(DATE_FORMAT))
                .append("</p>");

        html.append("<p><strong>To:</strong> ")
                .append(report.getToDate().format(DATE_FORMAT))
                .append("</p>");

        html.append("<p><strong>Generated At:</strong> ")
                .append(report.getGeneratedAt().format(DATE_TIME_FORMAT))
                .append("</p>");

        html.append("<hr>");

        html.append("<div class='summary'>");

        html.append("<p><strong>Total Transactions:</strong> ")
                .append(report.getTotalTransactions())
                .append("</p>");

        html.append("<p><strong>Total Deposits:</strong> ")
                .append(report.getTotalDeposits())
                .append("</p>");

        html.append("<p><strong>Total Withdrawals:</strong> ")
                .append(report.getTotalWithdrawals())
                .append("</p>");

        html.append("<p><strong>Total Transfers:</strong> ")
                .append(report.getTotalTransfers())
                .append("</p>");

        html.append("<p><strong>Total Deposit Amount:</strong> ₹")
                .append(report.getTotalDepositAmount())
                .append("</p>");

        html.append("<p><strong>Total Withdrawal Amount:</strong> ₹")
                .append(report.getTotalWithdrawalAmount())
                .append("</p>");

        html.append("<p><strong>Total Transfer Amount:</strong> ₹")
                .append(report.getTotalTransferAmount())
                .append("</p>");

        html.append("</div>");

        html.append("""
                <table>
                <tr>
                    <th>Transaction Id</th>
                    <th>Reference</th>
                    <th>Customer</th>
                    <th>Account</th>
                    <th>Type</th>
                    <th>Amount</th>
                    <th>Before</th>
                    <th>After</th>
                    <th>Time</th>
                </tr>
                """);

        for (TransactionAuditDTO transaction : report.getTransactions()) {

            html.append("<tr>");

            html.append("<td>")
                    .append(transaction.getTransactionId())
                    .append("</td>");

            html.append("<td>")
                    .append(transaction.getReferenceId())
                    .append("</td>");

            html.append("<td>")
                    .append(transaction.getCustomerName())
                    .append("</td>");

            html.append("<td>")
                    .append(transaction.getAccountNumber())
                    .append("</td>");

            html.append("<td>")
                    .append(transaction.getTransactionType())
                    .append("</td>");

            html.append("<td>₹")
                    .append(transaction.getAmount())
                    .append("</td>");

            html.append("<td>₹")
                    .append(transaction.getBalanceBefore())
                    .append("</td>");

            html.append("<td>₹")
                    .append(transaction.getBalanceAfter())
                    .append("</td>");

            html.append("<td>")
                    .append(transaction.getTransactionTime().format(DATE_TIME_FORMAT))
                    .append("</td>");

            html.append("</tr>");
        }

        html.append("""
                </table>

                <br>

                <p>
                This is an automatically generated audit report.
                Please do not reply to this email.
                </p>

                </div>

                </body>
                </html>
                """);

        return html.toString();
    }
}