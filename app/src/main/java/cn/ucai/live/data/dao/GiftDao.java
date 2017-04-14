package cn.ucai.live.data.dao;

import java.util.List;
import java.util.Map;

import cn.ucai.live.data.model.Gift;

/**
 * Created by w on 2017/4/14.
 */

public class GiftDao {
    public static final String GIFT_TABLE_NAME = "t_superwechat_gift";
    public static final String GIFT_COLUMN_ID = "m_gift_id";
    public static final String GIFT_COLUMN_NAME = "m_giftr_name";
    public static final String GIFT_COLUMN_URL = "m_gift_url";
    public static final String GIFT_COLUMN_PRICE = "m_giftr_price";

    public GiftDao() {
    }

    /**
     * save contact list
     *
     * @param giftList
     */
    public void saveAppGiftList(List<Gift> giftList) {
        DBManager.getInstance().saveAppGiftList(giftList);
    }

    /**
     * get contact list
     *
     * @return
     */
    public Map<Integer, Gift> getAppGiftList() {

        return DBManager.getInstance().getAppGiftList();
    }
}