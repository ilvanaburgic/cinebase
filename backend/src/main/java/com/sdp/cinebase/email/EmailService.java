package com.sdp.cinebase.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Service for sending email notifications to users.
 */
@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final String fromEmail;
    private final String fromName;

    public EmailService(JavaMailSender mailSender,
                        @Value("${app.email.from}") String fromEmail,
                        @Value("${app.email.name}") String fromName) {
        this.mailSender = mailSender;
        this.fromEmail = fromEmail;
        this.fromName = fromName;
    }

    /**
     * Send an email asynchronously to avoid blocking the main request thread.
     *
     * @param to Recipient email address
     * @param subject Email subject
     * @param htmlContent HTML email content
     */
    @Async
    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true = HTML

            mailSender.send(message);
            log.info("Email sent successfully to: {}", to);

        } catch (MessagingException e) {
            log.error("Failed to send email to: {}. Error: {}", to, e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error sending email to: {}. Error: {}", to, e.getMessage());
        }
    }

    /**
     * Send review confirmation email to user.
     *
     * @param userEmail User's email address
     * @param username User's name
     * @param mediaTitle Movie/TV show title
     * @param mediaType "movie" or "tv"
     * @param rating User's rating
     * @param reviewText Review content
     */
    @Async
    public void sendReviewConfirmation(String userEmail, String username,
                                       String mediaTitle, String mediaType,
                                       Double rating, String reviewText) {
        String subject = "Your review is live on CineBase — " + mediaTitle;
        String htmlContent = buildReviewConfirmationEmail(username, mediaTitle, mediaType, rating, reviewText);
        sendHtmlEmail(userEmail, subject, htmlContent);
    }

    /**
     * Build professional HTML email template for review confirmation.
     */
    private String buildReviewConfirmationEmail(String username, String mediaTitle,
                                                String mediaType, Double rating,
                                                String reviewText) {
        String mediaTypeDisplay = "movie".equals(mediaType) ? "Movie" : "TV Show";
        String stars = "★".repeat(rating.intValue()) + "☆".repeat(5 - rating.intValue());
        String displayReview = reviewText.length() > 300 ? reviewText.substring(0, 300) + "..." : reviewText;

        return String.format("""
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <meta http-equiv="X-UA-Compatible" content="IE=edge">
                    <title>Review Published</title>
                </head>
                <body style="margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif; background-color: #f4f4f5;">
                    <table width="100%%" cellpadding="0" cellspacing="0" style="background-color: #f4f4f5; padding: 40px 20px;">
                        <tr>
                            <td align="center">
                                <!-- Main Container -->
                                <table width="600" cellpadding="0" cellspacing="0" style="background-color: #ffffff; border-radius: 12px; box-shadow: 0 4px 12px rgba(0,0,0,0.08); overflow: hidden;">

                                    <!-- Header with Logo -->
                                    <tr>
                                        <td style="background-color: #ffffff; padding: 40px 30px; text-align: center; border-bottom: 3px solid #e74c3c;">
                                            <h1 style="margin: 0; color: #1f2937; font-size: 32px; font-weight: 700; letter-spacing: 1px;">
                                                🎬 CineBase
                                            </h1>
                                            <p style="margin: 8px 0 0 0; color: #6b7280; font-size: 14px;">
                                                Your Movie & TV Database
                                            </p>
                                        </td>
                                    </tr>

                                    <!-- Success Badge -->
                                    <tr>
                                        <td style="padding: 30px 30px 0 30px; text-align: center;">
                                            <div style="background-color: #10b981; color: white; padding: 12px 24px; border-radius: 50px; display: inline-block; font-weight: 600; font-size: 14px;">
                                                ✓ Review Published
                                            </div>
                                        </td>
                                    </tr>

                                    <!-- Greeting -->
                                    <tr>
                                        <td style="padding: 30px 30px 20px 30px;">
                                            <h2 style="margin: 0; color: #1f2937; font-size: 24px; font-weight: 600;">
                                                Hi %s,
                                            </h2>
                                            <p style="margin: 12px 0 0 0; color: #4b5563; font-size: 16px; line-height: 1.6;">
                                                Your review has been published successfully and is now visible to the CineBase community.
                                            </p>
                                        </td>
                                    </tr>

                                    <!-- Review Details Card -->
                                    <tr>
                                        <td style="padding: 0 30px;">
                                            <table width="100%%" cellpadding="0" cellspacing="0" style="background-color: #f9fafb; border-radius: 8px; border: 1px solid #e5e7eb;">
                                                <tr>
                                                    <td style="padding: 24px;">
                                                        <p style="margin: 0 0 8px 0; color: #6b7280; font-size: 12px; text-transform: uppercase; letter-spacing: 0.5px; font-weight: 600;">
                                                            Review Details
                                                        </p>

                                                        <h3 style="margin: 0 0 8px 0; color: #111827; font-size: 22px; font-weight: 700;">
                                                            %s
                                                        </h3>

                                                        <p style="margin: 0 0 12px 0; color: #6b7280; font-size: 14px;">
                                                            %s
                                                        </p>

                                                        <div style="margin: 16px 0;">
                                                            <span style="color: #fbbf24; font-size: 20px; letter-spacing: 2px;">%s</span>
                                                            <span style="margin-left: 8px; color: #6b7280; font-size: 14px; font-weight: 600;">%s / 5</span>
                                                        </div>

                                                        <div style="margin-top: 16px; padding-top: 16px; border-top: 1px solid #e5e7eb;">
                                                            <p style="margin: 0 0 8px 0; color: #6b7280; font-size: 12px; text-transform: uppercase; letter-spacing: 0.5px; font-weight: 600;">
                                                                Your Review
                                                            </p>
                                                            <p style="margin: 0; color: #374151; font-size: 15px; line-height: 1.7; font-style: italic;">
                                                                "%s"
                                                            </p>
                                                        </div>
                                                    </td>
                                                </tr>
                                            </table>
                                        </td>
                                    </tr>

                                    <!-- CTA Button -->
                                    <tr>
                                        <td style="padding: 30px; text-align: center;">
                                            <a href="http://localhost:3000/dashboard" style="display: inline-block; background-color: #e74c3c; color: #ffffff; text-decoration: none; padding: 14px 32px; border-radius: 8px; font-weight: 600; font-size: 15px; box-shadow: 0 2px 8px rgba(231, 76, 60, 0.3);">
                                                View Your Review
                                            </a>
                                        </td>
                                    </tr>

                                    <!-- What Happens Next -->
                                    <tr>
                                        <td style="padding: 0 30px 30px 30px;">
                                            <div style="background-color: #fef3c7; border-left: 4px solid #f59e0b; padding: 16px; border-radius: 4px;">
                                                <p style="margin: 0; color: #92400e; font-size: 14px; font-weight: 600;">
                                                    📢 What happens next
                                                </p>
                                                <p style="margin: 8px 0 0 0; color: #78350f; font-size: 13px; line-height: 1.6;">
                                                    Your review is now live and helping others discover great content. Thanks for being part of our community!
                                                </p>
                                            </div>
                                        </td>
                                    </tr>

                                    <!-- Footer -->
                                    <tr>
                                        <td style="background-color: #f9fafb; padding: 30px; text-align: center; border-top: 1px solid #e5e7eb;">
                                            <p style="margin: 0 0 12px 0; color: #6b7280; font-size: 13px;">
                                                Thanks for helping others discover great movies,
                                            </p>
                                            <p style="margin: 0 0 16px 0; color: #1f2937; font-size: 14px; font-weight: 600;">
                                                The CineBase Team
                                            </p>

                                            <div style="margin: 20px 0; padding-top: 20px; border-top: 1px solid #e5e7eb;">
                                                <p style="margin: 0; color: #9ca3af; font-size: 11px;">
                                                    © 2025 CineBase. All rights reserved.
                                                </p>
                                            </div>
                                        </td>
                                    </tr>

                                </table>
                            </td>
                        </tr>
                    </table>
                </body>
                </html>
                """,
                username,
                mediaTitle,
                mediaTypeDisplay,
                stars,
                rating,
                displayReview
        );
    }
}
