/******************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2006 ComPiere, Inc. All Rights Reserved.                *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * ComPiere, Inc., 2620 Augustine Dr. #245, Santa Clara, CA 95054, USA        *
 * or via info@compiere.org or http://www.compiere.org/license.html           *
 *****************************************************************************/
package org.compiere.model;

import java.sql.ResultSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.compiere.util.CCache;
import org.compiere.util.CLogger;
import org.compiere.util.EMail;
import org.compiere.util.Env;
import org.compiere.util.Msg;

/**
 * 	Web Store
 *  @author Jorg Janke
 *  @version $Id: MStore.java,v 1.4 2006/07/30 00:51:05 jjanke Exp $
 */
public class MStore extends X_W_Store
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5836212836465405633L;


	/**
	 * 	Get WStore from Cache
	 *	@param ctx context
	 *	@param W_Store_ID id
	 *	@return WStore
	 */
	public static MStore get (Properties ctx, int W_Store_ID)
	{
		Integer key = Integer.valueOf(W_Store_ID);
		MStore retValue = (MStore) s_cache.get (key);
		if (retValue != null)
			return retValue;
		retValue = new MStore (ctx, W_Store_ID, null);
		if (retValue.get_ID () != 0)
			s_cache.put (key, retValue);
		return retValue;
	}	//	get

	/**
	 * 	Get WStore from Cache
	 *	@param ctx context
	 *	@param contextPath web server context path
	 *	@return WStore
	 */
	public static MStore get (Properties ctx, String contextPath)
	{
		MStore wstore = null;
		Iterator<?> it = s_cache.values().iterator();
		while (it.hasNext())
		{
			wstore = (MStore)it.next();
			if (wstore.getWebContext().equals(contextPath))
				return wstore;
		}

		//	Search by context
		int cid = Env.getAD_Client_ID(Env.getCtx());
		try {
			if (cid > 0) {
				// forced potential cross tenant read - requires System client in context
				Env.setContext(Env.getCtx(), Env.AD_CLIENT_ID, 0);
			}
			wstore = new Query(Env.getCtx(), Table_Name, "WebContext=?", null)
					.setOnlyActiveRecords(true)
					.setParameters(contextPath)
					.first();
		} finally {
			if (cid > 0) {
				Env.setContext(Env.getCtx(), Env.AD_CLIENT_ID, cid);
			}
		}

		//	Try client
		if (wstore == null)
		{
			wstore = new Query(Env.getCtx(), Table_Name, "WebContext=?", null)
					.setOnlyActiveRecords(true)
					.setClient_ID()
					.setOrderBy(COLUMNNAME_W_Store_ID)
					.first();
			if (wstore != null) {
				s_log.warning("Context " + contextPath 
						+ " Not found - Found via AD_Client_ID=" + Env.getAD_Client_ID(ctx));
			}
		}
		//	Nothing
		if (wstore == null)
			return null;

		//	Save
		Integer key = Integer.valueOf(wstore.getW_Store_ID());
		s_cache.put (key, wstore);
		return wstore;
	}	//	get

	/**
	 * 	Get active Web Stores of Client
	 *	@param client client
	 *	@return array of web stores
	 */
	public static MStore[] getOfClient (MClient client)
	{
		List<MStore> list = new Query(Env.getCtx(), Table_Name, "AD_Client_ID=?", client.get_TrxName())
				.setOnlyActiveRecords(true)
				.setParameters(client.getAD_Client_ID())
				.list();
		//
		MStore[] retValue = new MStore[list.size ()];
		list.toArray (retValue);
		return retValue;
	}	//	getOfClient

	/**
	 * 	Get Active Web Stores
	 *	@return cached web stores
	 */
	public static MStore[] getActive()
	{
		s_log.fine("");
		try
		{
			Collection<?> cc = s_cache.values();
			Object[] oo = cc.toArray();
			for (int i = 0; i < oo.length; i++)
				if (s_log.isLoggable(Level.INFO)) s_log.info(i + ": " + oo[i]);
			MStore[] retValue = new MStore[oo.length];
			for (int i = 0; i < oo.length; i++)
				retValue[i] = (MStore)oo[i];
			return retValue;
		}
		catch (Exception e)
		{
			s_log.severe(e.toString());
		}
		return new MStore[]{};
	}	//	getActive

	/**	Cache						*/
	private static CCache<Integer,MStore> s_cache
		= new CCache<Integer,MStore>(Table_Name, 2);
	/**	Logger	*/
	private static CLogger	s_log	= CLogger.getCLogger (MStore.class);
	
	
	/**************************************************************************
	 * 	Standard Constructor
	 *	@param ctx context
	 *	@param W_Store_ID id
	 *	@param trxName trx
	 */
	public MStore (Properties ctx, int W_Store_ID, String trxName)
	{
		super (ctx, W_Store_ID, trxName);
		if (W_Store_ID == 0)
		{
			setIsDefault (false);
			setIsMenuAssets (true);	// Y
			setIsMenuContact (true);	// Y
			setIsMenuInterests (true);	// Y
			setIsMenuInvoices (true);	// Y
			setIsMenuOrders (true);	// Y
			setIsMenuPayments (true);	// Y
			setIsMenuRegistrations (true);	// Y
			setIsMenuRequests (true);	// Y
			setIsMenuRfQs (true);	// Y
			setIsMenuShipments (true);	// Y
			
		//	setC_PaymentTerm_ID (0);
		//	setM_PriceList_ID (0);
		//	setM_Warehouse_ID (0);
		//	setName (null);
		//	setSalesRep_ID (0);
		//	setURL (null);
		//	setWebContext (null);
		}	
	}	//	MWStore

	/**
	 * 	Load Constructor
	 *	@param ctx context
	 *	@param rs result set
	 *	@param trxName trx
	 */
	public MStore (Properties ctx, ResultSet rs, String trxName)
	{
		super (ctx, rs, trxName);
	}	//	MWStore
	
	/**	The Messages						*/
	private MMailMsg[]	m_msgs	= null;
	
	/**
	 * 	Get Web Context 
	 *	@param full if true fully qualified
	 *	@return web context
	 */
	public String getWebContext(boolean full)
	{
		if (!full)
			return super.getURL();
		String url = super.getURL();
		if (url == null || url.length() == 0)
			url = "http://localhost";
		if (url.endsWith("/"))
			url += url.substring(0, url.length()-1);
		return url + getWebContext();	//	starts with /
	}	//	getWebContext
	
	/**
	 * 	String Representation
	 *	@return info
	 */
	public String toString ()
	{
		StringBuilder sb = new StringBuilder ("WStore[");
		sb.append(getWebContext(true))
			.append ("]");
		return sb.toString ();
	}	//	toString
	
	
	/**
	 * 	Before Save
	 *	@param newRecord new
	 *	@return true if can be saved
	 */
	protected boolean beforeSave (boolean newRecord)
	{
		//	Context to start with /
		if (!getWebContext().startsWith("/"))
			setWebContext("/" + getWebContext());
		
		//	Org to Warehouse
		if (newRecord || is_ValueChanged("M_Warehouse_ID") || getAD_Org_ID() == 0)
		{
			MWarehouse wh = new MWarehouse (getCtx(), getM_Warehouse_ID(), get_TrxName());
			setAD_Org_ID(wh.getAD_Org_ID());
		}
		
		String url = getURL();
		if (url == null)
			url = "";
		boolean urlOK = url.startsWith("http://") || url.startsWith("https://");
		if (!urlOK) // || url.indexOf("localhost") != -1)
		{
			log.saveError("FillMandatory", Msg.getElement(getCtx(), "URL") 
				+ " - e.g. http://www.idempiere.org");
			return false;
		}
		
		return true;
	}	//	beforeSave
	
	
	/************
	 * 	Create EMail from Request User
	 *	@param to recipient
	 *	@param subject sunject
	 *	@param message nessage
	 *	@return EMail
	 */
	public EMail createEMail (String to, 
		String subject, String message)
	{
		if (to == null || to.length() == 0)
		{
			log.warning("No To");
			return null;
		}
		//
		MClient client = MClient.get(getCtx(), getAD_Client_ID());
		String from = getWStoreEMail();
		if (from == null || from.length() == 0)
			from = client.getRequestEMail();
		EMail email = new EMail (client,
				   from, to,
				   subject, message);
		//	Authorization
		if (client.isSmtpAuthorization())
		{
			if (getWStoreEMail() != null && getWStoreUser() != null && getWStoreUserPW() != null)
				email.createAuthenticator (getWStoreUser(), getWStoreUserPW());
			else
				email.createAuthenticator (client.getRequestUser(), client.getRequestUserPW());
		}
		//	Bcc
		email.addBcc(from);
		//
		return email;
	}	//	createEMail

	/**
	 * 	Send EMail from WebStore User
	 *	@param to recipient email address
	 *	@param subject subject
	 *	@param message message - add header & footer 
	 *	@return true if sent
	 */
	public boolean sendEMail (String to, 
		String subject, String message)
	{
		if (message == null || message.length() == 0)
		{
			log.warning("No Message");
			return false;
		}
		StringBuilder msgText = new StringBuilder();
		if (getEMailHeader() != null)
			msgText.append(getEMailHeader());
		msgText.append(message);
		if (getEMailFooter() != null)
			msgText.append(getEMailFooter());
		//
		EMail email = createEMail(to, subject, msgText.toString());
		if (email == null)
			return false;
		
		try
		{
			String msg = email.send();
			if (EMail.SENT_OK.equals (msg))
			{
				if (log.isLoggable(Level.INFO)) log.info("Sent EMail " + subject + " to " + to);
				return true;
			}
			else
			{
				log.warning("Could NOT Send Email: " + subject 
					+ " to " + to + ": " + msg
					+ " (" + getName() + ")");
				return false;
			}
		}
		catch (Exception ex)
		{
			log.severe(getName() + " - " + ex.getLocalizedMessage());
			return false;
		}
	}	//	sendEMail
	
	/**
	 * 	Test WebStore EMail
	 *	@return OK or error
	 */
	public String testEMail()
	{
		if (getWStoreEMail() == null || getWStoreEMail().length() == 0)
			return "No Web Store EMail for " + getName();
		//
		EMail email = createEMail (getWStoreEMail(),
			"iDempiere WebStore EMail Test", 
			"iDempiere WebStore EMail Test: " + toString());
		if (email == null)
			return "Could not create Web Store EMail: " + getName();
		try
		{
			String msg = email.send();
			if (EMail.SENT_OK.equals (msg))
			{
				if (log.isLoggable(Level.INFO)) log.info("Sent Test EMail to " + getWStoreEMail());
				return "OK";
			}
			else
			{
				log.warning("Could NOT send Test Email to " 
					+ getWStoreEMail() + ": " + msg);
				return msg;
			}
		}
		catch (Exception ex)
		{
			log.severe(getName() + " - " + ex.getLocalizedMessage());
			return ex.getLocalizedMessage();
		}
	}	//	testEMail

	/**
	 * 	Get Messages
	 *	@param reload reload data
	 *	@return array of messages
	 */
	public MMailMsg[] getMailMsgs (boolean reload)
	{
		if (m_msgs != null && !reload)
			return m_msgs;
		List<MMailMsg> list = new Query(Env.getCtx(), MMailMsg.Table_Name, "W_Store_ID=?", get_TrxName())
				.setParameters(getW_Store_ID())
				.setOrderBy(MMailMsg.COLUMNNAME_MailMsgType)
				.list();
		//
		m_msgs = new MMailMsg[list.size ()];
		list.toArray (m_msgs);
		return m_msgs;
	}	//	getMailMsgs

	/**
	 * 	Get Mail Msg and if not found create it
	 *	@param MailMsgType mail message type
	 *	@return message
	 */
	public MMailMsg getMailMsg (String MailMsgType)
	{
		if (m_msgs == null)
			getMailMsgs(false);
		
		//	existing msg
		for (int i = 0; i < m_msgs.length; i++)
		{
			if (m_msgs[i].getMailMsgType().equals(MailMsgType))
				return m_msgs[i];
		}
		
		//	create missing
		if (createMessages() == 0)
		{
			log.severe("Not created/found: " + MailMsgType);
			return null;
		}
		getMailMsgs(true);
		//	try again
		for (int i = 0; i < m_msgs.length; i++)
		{
			if (m_msgs[i].getMailMsgType().equals(MailMsgType))
				return m_msgs[i];
		}
		
		//	nothing found
		log.severe("Not found: " + MailMsgType);
		return null;
	}	//	getMailMsg
	
	
	/**************************************************************************
	 * 	Create (missing) Messages
	 * 	@return number of messages created
	 */
	public int createMessages()
	{
		String[][] initMsgs = new String[][]
		{
			new String[]{MMailMsg.MAILMSGTYPE_UserVerification,
				"EMail Verification", 
				"EMail Verification ",
				"Dear ", 
				"\nYou requested the Verification Code: ",
				"\nPlease enter the verification code to get access."},
			new String[]{MMailMsg.MAILMSGTYPE_UserPassword,
				"Password Request", 
				"Password Request ",
				"Dear ", 
				"\nWe received a 'Send Password' request from: ",
				"\nYour password is: "},
			new String[]{MMailMsg.MAILMSGTYPE_Subscribe,
				"Subscription New", 
				"New Subscription ",
				"Dear ", 
				"\nYou requested to be added to the list: ",
				"\nThanks for your interest."},
			new String[]{MMailMsg.MAILMSGTYPE_UnSubscribe,
				"Subscription Removed", 
				"Remove Subscription ",
				"Dear ", 
				"\nYou requested to be removed from the list: ",
				"\nSorry to see you go.  This is effictive immediately."},
			new String[]{MMailMsg.MAILMSGTYPE_OrderAcknowledgement,
				"Order Acknowledgement", 
				"iDempiere Web - Order ",
				"Dear ", 
				"\nThank you for your purchase: ",
				"\nYou can view your Orders, Invoices, Payments in the Web Store."
				+ "\nFrom there, you also download your Assets (Documentation, etc.)"},
			new String[]{MMailMsg.MAILMSGTYPE_PaymentAcknowledgement,
				"Payment Success", 
				"iDempiere Web - Payment ",
				"Dear ", 
				"\nThank you for your payment of ",
				"\nYou can view your Orders, Invoices, Payments in the Web Store."
				+ "\nFrom there you also download your Assets (Documentation, etc.)"},
			new String[]{MMailMsg.MAILMSGTYPE_PaymentError,
				"Payment Error", 
				"iDempiere Web - Declined Payment ",
				"Dear ",
				"\nUnfortunately your payment was declined: ",
				"\nPlease check and try again. You can pay later by going to 'My Orders' or 'My Invoices' - or by directly creating a payment in 'My Payments'"},
			new String[]{MMailMsg.MAILMSGTYPE_Request,
				"Request", 
				"Request ",
				"Dear ",
				"\nThank you for your request: " + MRequest.SEPARATOR,
				MRequest.SEPARATOR + "\nPlease check back for updates."},
				
			new String[]{MMailMsg.MAILMSGTYPE_UserAccount,
				"Welcome Message", 
				"Welcome",
				"Welcome to our Web Store",
				"This is the Validation Code to access information:",
				""},
		};
		
		if (m_msgs == null)
			getMailMsgs(false);
		if (m_msgs.length == initMsgs.length)	//	may create a problem if user defined own ones - unlikely
			return 0;		//	nothing to do
		
		int counter = 0;
		for (int i = 0; i < initMsgs.length; i++)
		{
			boolean found = false;
			for (int m = 0; m < m_msgs.length; m++)
			{
				if (initMsgs[i][0].equals(m_msgs[m].getMailMsgType()))
				{
					found = true;
					break;
				}
			}	//	for all existing msgs
			if (found)
				continue;
			MMailMsg msg = new MMailMsg(this, initMsgs[i][0], initMsgs[i][1], 
				initMsgs[i][2], initMsgs[i][3], initMsgs[i][4], initMsgs[i][5]);
			if (msg.save())
				counter++;
			else
				log.severe("Not created MailMsgType=" + initMsgs[i][0]);
		}	//	for all initMsgs
		
		if (log.isLoggable(Level.INFO)) log.info("#" + counter);
		m_msgs = null;		//	reset
		return counter;
	}	//	createMessages
	
}	//	MWStore
