/**
 * 
 */
package org.adempiere.webstore.factory;

import java.sql.ResultSet;

import org.adempiere.base.IModelFactory;
import org.compiere.model.I_W_Advertisement;
import org.compiere.model.I_W_Basket;
import org.compiere.model.I_W_BasketLine;
import org.compiere.model.I_W_Click;
import org.compiere.model.I_W_ClickCount;
import org.compiere.model.I_W_Counter;
import org.compiere.model.I_W_CounterCount;
import org.compiere.model.I_W_MailMsg;
import org.compiere.model.I_W_Store;
import org.compiere.model.MAdvertisement;
import org.compiere.model.MClick;
import org.compiere.model.MClickCount;
import org.compiere.model.MCounterCount;
import org.compiere.model.MMailMsg;
import org.compiere.model.MStore;
import org.compiere.model.PO;
import org.compiere.model.X_W_Basket;
import org.compiere.model.X_W_BasketLine;
import org.compiere.model.X_W_Counter;
import org.compiere.util.Env;
import org.osgi.service.component.annotations.Component;

/**
 * @author hengsin
 *
 */
@Component(immediate = true, service = IModelFactory.class)
public class WebstoreModelFactory implements IModelFactory {

	/**
	 * 
	 */
	public WebstoreModelFactory() {
	}

	@Override
	public Class<?> getClass(String tableName) {
		switch (tableName) {
			case I_W_Advertisement.Table_Name:
				return I_W_Advertisement.class;
			case I_W_Basket.Table_Name:
				return I_W_Basket.class;
			case I_W_BasketLine.Table_Name:
				return I_W_BasketLine.class;
			case I_W_Click.Table_Name:
				return I_W_Click.class;
			case I_W_ClickCount.Table_Name:
				return I_W_ClickCount.class;
			case I_W_Counter.Table_Name:
				return I_W_Counter.class;
			case I_W_CounterCount.Table_Name:
				return I_W_CounterCount.class;
			case I_W_MailMsg.Table_Name:
				return I_W_MailMsg.class;
			case I_W_Store.Table_Name:
				return I_W_Store.class;
		}
		return null;
	}

	@Override
	public PO getPO(String tableName, int Record_ID, String trxName) {
		switch (tableName) {
			case I_W_Advertisement.Table_Name:
				return new MAdvertisement(Env.getCtx(), Record_ID, trxName);
			case I_W_Basket.Table_Name:
				return new X_W_Basket(Env.getCtx(), Record_ID, trxName);
			case I_W_BasketLine.Table_Name:
				return new X_W_BasketLine(Env.getCtx(), Record_ID, trxName);
			case I_W_Click.Table_Name:
				return new MClick(Env.getCtx(), Record_ID, trxName);
			case I_W_ClickCount.Table_Name:
				return new MClickCount(Env.getCtx(), Record_ID, trxName);
			case I_W_Counter.Table_Name:
				return new X_W_Counter(Env.getCtx(), Record_ID, trxName);
			case I_W_CounterCount.Table_Name:
				return new MCounterCount(Env.getCtx(), Record_ID, trxName);
			case I_W_MailMsg.Table_Name:
				return new MMailMsg(Env.getCtx(), Record_ID, trxName);
			case I_W_Store.Table_Name:
				return new MStore(Env.getCtx(), Record_ID, trxName);
		}
		return null;
	}

	@Override
	public PO getPO(String tableName, ResultSet rs, String trxName) {
		switch (tableName) {
		case I_W_Advertisement.Table_Name:
			return new MAdvertisement(Env.getCtx(), rs, trxName);
		case I_W_Basket.Table_Name:
			return new X_W_Basket(Env.getCtx(), rs, trxName);
		case I_W_BasketLine.Table_Name:
			return new X_W_BasketLine(Env.getCtx(), rs, trxName);
		case I_W_Click.Table_Name:
			return new MClick(Env.getCtx(), rs, trxName);
		case I_W_ClickCount.Table_Name:
			return new MClickCount(Env.getCtx(), rs, trxName);
		case I_W_Counter.Table_Name:
			return new X_W_Counter(Env.getCtx(), rs, trxName);
		case I_W_CounterCount.Table_Name:
			return new MCounterCount(Env.getCtx(), rs, trxName);
		case I_W_MailMsg.Table_Name:
			return new MMailMsg(Env.getCtx(), rs, trxName);
		case I_W_Store.Table_Name:
			return new MStore(Env.getCtx(), rs, trxName);
	}
	return null;
	}

}
