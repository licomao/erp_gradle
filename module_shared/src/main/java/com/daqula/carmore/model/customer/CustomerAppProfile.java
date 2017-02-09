package com.daqula.carmore.model.customer;

import com.daqula.carmore.model.shop.Shop;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.io.IOException;

/** App顾客信息，对绑定了门店的组织可见 */
@Entity
@DiscriminatorValue(value=CustomerProfile.PROFILE_TYPE_APP)
public class CustomerAppProfile extends CustomerProfile {

    /** 昵称，默认为手机号 */
    @Column
    public String nickName;

    /** 头像Url */
    public String avatarUrl;

    /** 性别，男=0，女=1 */
    public Integer gender = 0;

    /** 当前绑定门店 */
    @ManyToOne
    public Shop bindingShop;

    //**************************************************************************
    // Serialization / Deserialization
    //**************************************************************************

    public static class NickNameSerializer extends JsonSerializer<CustomerAppProfile> {
        @Override
        public void serialize(CustomerAppProfile value, JsonGenerator jgen, SerializerProvider provider)
                throws IOException {
            jgen.writeString(value.nickName);
        }
    }
}
