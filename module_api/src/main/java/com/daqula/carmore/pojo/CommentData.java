package com.daqula.carmore.pojo;

import com.daqula.carmore.annotation.ApiJsonIgnore;
import com.daqula.carmore.model.BaseEntity;
import com.daqula.carmore.model.customer.CustomerAppProfile;
import com.daqula.carmore.model.order.Comment;
import com.daqula.carmore.model.order.SettleOrder;
import com.daqula.carmore.model.shop.Shop;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.joda.time.DateTime;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 评论
 */
public class CommentData {

    public String customer;

    public float rating;

    public String comment;

    public String customerAvatarUrl;

    public DateTime date;

    public static CommentData build(Comment comment) {
        CommentData data = new CommentData();
        data.customer = comment.customer.nickName;
        data.rating = (comment.rating1 + comment.rating2 + comment.rating3
                + comment.rating4 + comment.rating5 + comment.rating6 + comment.rating7) / 7f;
        data.comment = comment.comment;
        data.customerAvatarUrl = comment.customer.avatarUrl;
        data.date = comment.createdDate;
        return data;
    }

    public static List<CommentData> build(List<Comment> comments) {
        return comments.stream().map(CommentData::build).collect(Collectors.toList());
    }
}
