package org.adempiere.webstore.util;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.ecs.AlignType;
import org.apache.ecs.xhtml.body;
import org.apache.ecs.xhtml.br;
import org.apache.ecs.xhtml.hr;
import org.apache.ecs.xhtml.p;
import org.apache.ecs.xhtml.small;
import org.compiere.model.MMailMsg;
import org.compiere.model.MRequest;
import org.compiere.model.MStore;
import org.compiere.model.MUserMail;
import org.compiere.util.CLogger;
import org.compiere.util.EMail;
import org.compiere.util.Msg;
import org.compiere.util.WebDoc;
import org.compiere.util.WebUser;
import org.compiere.util.WebUtil;

public final class WebstoreUtil {

	private final static CLogger log = CLogger.getCLogger(WebstoreUtil.class);
	
	private WebstoreUtil() {
	}

	/**************************************************************************
	 * 	Send EMail
	 *	@param request request
	 *	@param to web user
	 *	@param msgType see MMailMsg.MAILMSGTYPE_*
	 *	@param parameter object array with parameters
	 * 	@return mail EMail.SENT_OK or error message 
	 */
	public static String sendEMail (HttpServletRequest request, WebUser to,
		String msgType, Object[] parameter)
	{
		WebSessionCtx wsc = WebSessionCtx.get(request);
		MStore wStore = wsc.wstore;
		MMailMsg mailMsg = wStore.getMailMsg(msgType);
		//
		StringBuilder subject = new StringBuilder(mailMsg.getSubject());
		if (parameter.length > 0 && parameter[0] != null)
			subject.append(parameter[0]);
		//
		StringBuilder message = new StringBuilder();
		String hdr = wStore.getEMailFooter();
		if (hdr != null && hdr.length() > 0)
			message.append(hdr).append("\n");
		message.append(mailMsg.getMessage());
		if (parameter.length > 1 && parameter[1] != null)
			message.append(parameter[1]);
		if (mailMsg.getMessage2() != null)
		{
			message.append("\n")
				.append(mailMsg.getMessage2());
			if (parameter.length > 2 && parameter[2] != null)
				message.append(parameter[2]);
		}
		if (mailMsg.getMessage3() != null)
		{
			message.append("\n")
				.append(mailMsg.getMessage3());
			if (parameter.length > 3 && parameter[3] != null)
				message.append(parameter[3]);
		}
		message.append(MRequest.SEPARATOR)
			.append("http://").append(request.getServerName()).append(request.getContextPath())
			.append("/ - ").append(wStore.getName())
			.append("\n").append("Request from: ").append(WebUtil.getFrom(request))
			.append("\n");
		String ftr = wStore.getEMailFooter();
		if (ftr != null && ftr.length() > 0)
			message.append(ftr);
		
		//	Create Mail
		EMail email = wStore.createEMail(to.getEmail(), 
			subject.toString(), message.toString());
		//	CC Order
		if (msgType.equals(MMailMsg.MAILMSGTYPE_OrderAcknowledgement))
		{
			String orderEMail = wStore.getWebOrderEMail();
			String storeEMail = wStore.getWStoreEMail();
			if (orderEMail != null && orderEMail.length() > 0
				&& !orderEMail.equals(storeEMail))	//	already Bcc
				email.addBcc(orderEMail);
		}

		//	Send
		String retValue = email.send();
		//	Log
		MUserMail um = newUserMail(mailMsg, to.getAD_User_ID(), email);
		um.saveEx();
		//
		return retValue;
	}	//	sendEMail
	
	/**
	 * 	Resend Validation Code
	 * 	@param request request
	 *	@param wu user
	 */
	public static void resendCode(HttpServletRequest request, WebUser wu)
	{
		String msg = sendEMail(request, wu, 
			MMailMsg.MAILMSGTYPE_UserVerification,
			new Object[]{
				request.getServerName(),
				wu.getName(),
				wu.getEMailVerifyCode()});
		if (EMail.SENT_OK.equals(msg))
			wu.setPasswordMessage ("EMail sent");
		else
			wu.setPasswordMessage ("Problem sending EMail: " + msg);
	}	//	resendCode
	
	/**
	 * 	Parent Constructor
	 *	@param parent Mail message
	 *	@param AD_User_ID recipient user
	 *	@param mail email
	 */
	public static MUserMail newUserMail(MMailMsg parent, int AD_User_ID, EMail mail)
	{
		MUserMail userMail = new MUserMail(parent.getCtx(), 0, parent.get_TrxName());
		userMail.set_ValueNoCheck ("AD_Client_ID", Integer.valueOf(parent.getAD_Client_ID()));
		userMail.set_ValueNoCheck ("AD_Org_ID", Integer.valueOf(parent.getAD_Org_ID()));
		userMail.setAD_User_ID(AD_User_ID);
		userMail.setW_MailMsg_ID(parent.getW_MailMsg_ID());
		userMail.setSenderAndRecipient(mail);
		//
		if (mail.isSentOK())
			userMail.setMessageID(mail.getMessageID());
		else
		{
			userMail.setMessageID(mail.getSentMsg());
			userMail.setIsDelivered(MUserMail.ISDELIVERED_No);
		}
		
		return userMail;
	}
	
	/**
	 *  Create Timeout Message
	 *
	 *  @param request request
	 *  @param response response
	 *  @param servlet servlet
	 *  @param message - optional message
	 *  @throws ServletException
	 *  @throws IOException
	 */
	public static void createTimeoutPage (HttpServletRequest request, HttpServletResponse response,
		HttpServlet servlet, String message) throws ServletException, IOException
	{
		log.info(message);
	  	WebSessionCtx wsc = WebSessionCtx.get(request);
		String windowTitle = "Timeout";
		if (wsc != null)
			windowTitle = Msg.getMsg(wsc.ctx, "Timeout");

		WebDoc doc = WebDoc.create (windowTitle);

		//	Body
		body body = doc.getBody();
		//  optional message
		if (message != null && message.length() > 0)
			body.addElement(new p(message, AlignType.CENTER));

		//  login button
		body.addElement(WebUtil.getLoginButton(wsc == null ? null : wsc.ctx));

		//
		body.addElement(new hr());
		body.addElement(new small(servlet.getClass().getName()));
		//	fini
		WebUtil.createResponse (request, response, servlet, null, doc, false);
	}   //  createTimeoutPage

	/**
	 *  Create Error Message
	 *
	 *  @param request request
	 *  @param response response
	 *  @param servlet servlet
	 *  @param message message
	 *  @throws ServletException
	 *  @throws IOException
	 */
	public static void createErrorPage (HttpServletRequest request, HttpServletResponse response,
		HttpServlet servlet, String message) 
		throws ServletException, IOException
	{
		log.info( message);
	  	WebSessionCtx wsc = WebSessionCtx.get(request);
		String windowTitle = "Error";
		if (wsc != null)
			windowTitle = Msg.getMsg(wsc.ctx, "Error");
		if (message != null)
			windowTitle += ": " + message;

		WebDoc doc = WebDoc.create (windowTitle);

		//	Body
		body b = doc.getBody();

		b.addElement(new p(servlet.getServletName(), AlignType.CENTER));
		b.addElement(new br());

		//	fini
		WebUtil.createResponse (request, response, servlet, null, doc, false);
	}   //  createErrorPage
	
	/**
     * 	reload
     *	@param logMessage
     *	@param jsp
     *	@param session
     *	@param request
     *	@param response
     *	@param thisContext
     *	@throws ServletException
     *	@throws IOException
     */
    public static void reload(String logMessage, String jsp, HttpSession session, HttpServletRequest request, HttpServletResponse response, ServletContext thisContext)
            throws ServletException, IOException
    {
        session.setAttribute(WebSessionCtx.HDR_MESSAGE, logMessage);
        log.warning(" - " + logMessage + " - update not confirmed");
        thisContext.getRequestDispatcher(jsp).forward(request, response);
    }

}
