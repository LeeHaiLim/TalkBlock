package com.dev.block.data.network

import com.dev.block.BuildConfig
import java.io.InputStreamReader
import java.util.Properties
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class EmailSender : Authenticator() {
    private val fromEmail: String = BuildConfig.EMAIL_ID
    private val fromPassword: String = BuildConfig.EMAIL_PW

    private val emailTemplate by lazy {
        javaClass.classLoader?.getResourceAsStream("assets/email_template.html")
            ?.let { InputStreamReader(it) }?.readText()
    }

    private val properties = Properties().apply {
        put("mail.smtp.host", "smtp.gmail.com")
        put("mail.smtp.port", "587")
        put("mail.smtp.auth", "true")
        put("mail.smtp.starttls.enable", "true")
        put("mail.smtp.ssl.protocols", "TLSv1.2")
    }

    private val session = Session.getDefaultInstance(properties, this)

    override fun getPasswordAuthentication(): PasswordAuthentication {
        return PasswordAuthentication(fromEmail, fromPassword)
    }

    fun sendEmail(
        to: String,
        title: String,
        content: String,
        contentDescription: String,
        extraDescription: String
    ) {
        val message = MimeMessage(session).apply {
            sender = (InternetAddress(fromEmail))
            subject = title
            addRecipients(Message.RecipientType.TO, InternetAddress.parse(to))
            setContent(
                emailTemplate
                    ?.replace("{{title}}", title)
                    ?.replace("{{content}}", content)
                    ?.replace("{{content description}}", contentDescription)
                    ?.replace("{{extra description}}", extraDescription)
                    ?: content,
                "text/html; charset=utf-8"
            )
        }

        Transport.send(message)
    }
}
