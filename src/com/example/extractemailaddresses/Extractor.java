
package com.example.extractemailaddresses;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;

import android.util.Log;

public class Extractor {

    private Extractor() {
        throw new AssertionError();
    }

    public static final String TAG = Extractor.class.getSimpleName();

    public static void run(ArrayList<String> addressList, String userEmail, String userPassword)
            throws UnsupportedEncodingException {

        Properties props = new Properties();
        props.setProperty("mail.store.protocol", "imaps");
        try {

            Session session = Session.getDefaultInstance(props, null);
            Store store = session.getStore();
            store.connect("imap.gmail.com", userEmail,
                    userPassword);
            Folder inbox = store.getFolder("[Gmail]/Sent Mail");
            inbox.open(Folder.READ_ONLY);
            Message[] messages = inbox.getMessages();

            for (Message message : messages) {
                String decodedNameAndEmailAddress = extractNameAndEmailAddress(message);
                if (decodedNameAndEmailAddress != null) {
                    addressList.add(decodedNameAndEmailAddress);
                }
            }
            inbox.close(false);
            store.close();

        } catch (NoSuchProviderException nspe) {
            Log.e(TAG, "invalid provider name");
        } catch (MessagingException me) {
            Log.e(TAG, "messaging exception");
            me.printStackTrace();
        }
    }

    private static String extractNameAndEmailAddress(Message sentMessages)
            throws MessagingException {
        Address[] addressArray = sentMessages.getAllRecipients();
        if (addressArray == null) {
            return null;
        }
        int size = addressArray.length;
        StringBuilder receiptientEmailAddress = new StringBuilder();
        for (Address address : addressArray) {

            if (receiptientEmailAddress.length() != 0) {
                receiptientEmailAddress.append(" ");
                receiptientEmailAddress.append(filteringInformation((InternetAddress) address));
                if (--size != 0) {
                    receiptientEmailAddress.append("\n");
                }
            }
            else {
                receiptientEmailAddress.append(filteringInformation((InternetAddress) address));
                if (--size != 0) {
                    receiptientEmailAddress.append("\n");
                }
            }

        }
        return receiptientEmailAddress.toString();
    }

    private static String filteringInformation(InternetAddress internetAddress) {

        StringBuilder nameAndEmailAddress = new StringBuilder();
        if (internetAddress.getPersonal() == null || internetAddress.getPersonal().contains("@")) {
            nameAndEmailAddress.append("No Name - ");
            nameAndEmailAddress.append(internetAddress.getAddress());
        }
        else {
            nameAndEmailAddress.append(internetAddress.getPersonal());
            nameAndEmailAddress.append(" - ");
            nameAndEmailAddress.append(internetAddress.getAddress());
        }
        return nameAndEmailAddress.toString();

    }
}
