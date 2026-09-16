package com.passauditor.reporting;

import com.passauditor.entropy.CrackTimeEstimator;
import com.passauditor.model.PasswordReport;
import com.passauditor.model.PasswordStrength;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Renders the audit as a self-contained HTML page so the user can save and
 * share the results. The HTML is generated with string templates - no external
 * dependency required - and uses inline CSS so the file works offline.
 */
public class HtmlReporter {

    private static final CrackTimeEstimator TIME_FMT = new CrackTimeEstimator();

    public void write(List<PasswordReport> reports, Path output) throws IOException {
        StringBuilder sb = new StringBuilder(8192);
        sb.append("<!DOCTYPE html>")
                .append("<html lang=\"en\"><head><meta charset=\"utf-8\">")
                .append("<title>Password Strength Audit</title><style>")
                .append("body{font-family:'Segoe UI',Arial,sans-serif;margin:2rem auto;max-width:920px;color:#1f2933}")
                .append("h1{color:#1d4ed8}table{border-collapse:collapse;width:100%;margin-top:1rem}")
                .append("th,td{border:1px solid #cbd2d9;padding:.55rem .7rem;text-align:left;vertical-align:top}")
                .append("th{background:#eef2ff}")
                .append(".badge{padding:.15rem .5rem;border-radius:6px;font-size:.8rem;font-weight:600;color:#fff}")
                .append(".VERY_WEAK,.WEAK{background:#dc2626}.FAIR{background:#d97706}.STRONG,.VERY_STRONG{background:#16a34a}")
                .append("small{color:#52606d}</style></head><body>")
                .append("<h1>Password Strength Audit</h1>")
                .append("<p>Generated locally. No password data left your machine.</p>")
                .append("<table><thead><tr><th>#</th><th>Password (masked)</th><th>Length</th><th>Classes</th>")
                .append("<th>Entropy</th><th>Est. crack time</th><th>Strength</th><th>Patterns / Warnings</th></tr></thead><tbody>");

        int idx = 1;
        for (PasswordReport r : reports) {
            String notes = String.join("; ", r.getMatchedPatterns());
            if (!r.getWarnings().isEmpty()) {
                if (!notes.isEmpty()) notes += " | ";
                notes += "Warnings: " + String.join("; ", r.getWarnings());
            }
            sb.append("<tr>")
                    .append("<td>").append(idx++).append("</td>")
                    .append("<td><code>").append(escape(ConsoleReporter.mask(r.getPassword()))).append("</code></td>")
                    .append("<td>").append(r.getLength()).append("</td>")
                    .append("<td>").append(r.getCharClassCount()).append("</td>")
                    .append("<td>").append(String.format("%.1f", r.getEntropyBits())).append(" bits</td>")
                    .append("<td>").append(TIME_FMT.humanReadable(r.getCrackTimeSeconds())).append("</td>")
                    .append("<td><span class=\"badge ").append(r.getStrength().name()).append("\">")
                    .append(r.getStrength().getLabel()).append("</span></td>")
                    .append("<td><small>").append(escape(notes)).append("</small></td>")
                    .append("</tr>");
        }
        sb.append("</tbody></table>");

        int weak = 0, strong = 0;
        double avg = 0;
        for (PasswordReport r : reports) {
            avg += r.getEntropyBits();
            if (r.getStrength() == PasswordStrength.VERY_WEAK || r.getStrength() == PasswordStrength.WEAK) weak++;
            if (r.getStrength() == PasswordStrength.STRONG || r.getStrength() == PasswordStrength.VERY_STRONG) strong++;
        }
        avg = reports.isEmpty() ? 0 : avg / reports.size();
        sb.append("<p><strong>Summary:</strong> ")
                .append(reports.size()).append(" passwords audited, average entropy ")
                .append(String.format("%.1f", avg)).append(" bits, ")
                .append(weak).append(" weak, ").append(strong).append(" strong.</p>")
                .append("</body></html>");

        Files.writeString(output, sb.toString(), StandardCharsets.UTF_8);
    }

    private static String escape(String s) {
        if (s == null) return "";
        StringBuilder out = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '&' -> out.append("&amp;");
                case '<' -> out.append("&lt;");
                case '>' -> out.append("&gt;");
                case '"' -> out.append("&quot;");
                default -> out.append(c);
            }
        }
        return out.toString();
    }
}
