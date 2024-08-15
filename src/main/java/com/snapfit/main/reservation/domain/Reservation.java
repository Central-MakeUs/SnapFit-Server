package com.snapfit.main.reservation.domain;


import com.snapfit.main.common.domain.location.Location;
import com.snapfit.main.common.domain.vibe.Vibe;
import com.snapfit.main.post.domain.PostImage;
import com.snapfit.main.post.domain.PostPrice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table("reservation")
@Builder
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class Reservation {

    @Id
    private Long id;

    @Column("user_id")
    private Long userId;

    @Column("post_id")
    private Long postId;

    @Column("maker_id")
    private Long makerId;

    @Column("minute_price")
    private Integer minutesPrice;

    @Column("minutes")
    private Integer minutes;

    @Column("person")
    private Integer person;

    @Column("person_price")
    private Integer personPrice;


    @Column("cancel_message")
    private String cancelMessage;

    @Column("reserve_location")
    private String reserveLocation;

    @Column("created_at")
    @CreatedDate
    private LocalDateTime createAt;

    @Column("reservation_time")
    private LocalDateTime reservationTime;

    @Column("email")
    private String email;

    @Column("phone_number")
    private String phoneNumber;

    public void setCancelMessage(String cancelMessage) {
        this.cancelMessage = cancelMessage;
    }
}
