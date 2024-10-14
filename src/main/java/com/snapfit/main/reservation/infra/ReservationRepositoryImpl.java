package com.snapfit.main.reservation.infra;

import com.snapfit.main.common.dto.PageResult;
import com.snapfit.main.reservation.domain.Reservation;
import com.snapfit.main.reservation.domain.ReservationRepository;

import io.r2dbc.spi.Parameter;
import io.r2dbc.spi.Parameters;
import io.r2dbc.spi.R2dbcType;
import lombok.RequiredArgsConstructor;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Repository
@RequiredArgsConstructor
public class ReservationRepositoryImpl implements ReservationRepository {

    private final DatabaseClient databaseClient;

    //TODO 추후에 fk로 설정할 필요 있음. key 변경되면 과거 데이터를 쓰지 못하게 되므로.
    private Mono<String> getEncryptionKey() {
        return databaseClient.sql("SELECT my_key FROM key_config ORDER BY created_at DESC LIMIT 1")
            .map(row -> row.get("my_key", String.class))
            .one()
            .map(key -> {
                System.out.println(key);
                return key;
            });
    }

    @Override
    public Mono<Reservation> save(Reservation reservation) {
        return getEncryptionKey().flatMap(encryptionKey -> {
                DatabaseClient.GenericExecuteSpec sql = databaseClient.sql("""
                        INSERT INTO reservation (user_id, post_id, maker_id, minute_price, minutes, person, person_price, 
                                                 cancel_message, reserve_location, reservation_time, email, phone_number)
                        VALUES (:userId, :postId, :makerId, :minutesPrice, :minutes, :person, :personPrice, 
                                :cancelMessage, :reserveLocation, :reservationTime, 
                                pgp_sym_encrypt(:email, :encryptionKey), 
                                pgp_sym_encrypt(:phoneNumber, :encryptionKey))
                        RETURNING *
                        """)
                    .bind("userId", reservation.getUserId())
                    .bind("postId", reservation.getPostId())
                    .bind("makerId", reservation.getMakerId())
                    .bind("minutesPrice", reservation.getMinutesPrice())
                    .bind("minutes", reservation.getMinutes())
                    .bind("person", reservation.getPerson())
                    .bind("personPrice", reservation.getPersonPrice())
                    .bind("cancelMessage", Parameters.in(R2dbcType.VARCHAR, reservation.getCancelMessage()))
                    .bind("reserveLocation", reservation.getReserveLocation())
                    .bind("reservationTime", reservation.getReservationTime())
                    .bind("email", reservation.getEmail())
                    .bind("phoneNumber", reservation.getPhoneNumber())
                    .bind("encryptionKey", encryptionKey);
                //
                //                    if (reservation.getCancelMessage() == null) {
                //                        sql.bindNull("cancelMessage", String.class);
                //                    } else {
                //                        sql.bind("cancelMessage", reservation.getCancelMessage());
                //
                //                    }

                return sql.map((row, metadata) -> Reservation.builder()
                        .id(row.get("id", Long.class))
                        .userId(row.get("user_id", Long.class))
                        .postId(row.get("post_id", Long.class))
                        .makerId(row.get("maker_id", Long.class))
                        .minutesPrice(row.get("minute_price", Integer.class))
                        .minutes(row.get("minutes", Integer.class))
                        .person(row.get("person", Integer.class))
                        .personPrice(row.get("person_price", Integer.class))
                        .cancelMessage(row.get("cancel_message", String.class))
                        .reserveLocation(row.get("reserve_location", String.class))
                        .createAt(row.get("created_at", LocalDateTime.class))
                        .reservationTime(row.get("reservation_time", LocalDateTime.class))
                        .email(reservation.getEmail())
                        .phoneNumber(reservation.getPhoneNumber())
                        .build()
                    )
                    .one();

            }
        );
    }

    @Override
    public Mono<PageResult<Reservation>> findByUserId(int limit, int offset, long userId) {
        return getEncryptionKey().flatMap(encryptionKey ->
            databaseClient.sql("""
                    SELECT id, user_id, post_id, maker_id, minute_price, minutes, person, person_price, 
                           cancel_message, reserve_location, created_at, reservation_time,
                           pgp_sym_decrypt(email::bytea, :encryptionKey) AS email, 
                           pgp_sym_decrypt(phone_number::bytea, :encryptionKey) AS phone_number
                    FROM reservation 
                    WHERE user_id = :userId 
                    ORDER BY reservation_time DESC 
                    LIMIT :limit OFFSET :offset
                    """)
                .bind("userId", userId)
                .bind("limit", limit)
                .bind("offset", offset)
                .bind("encryptionKey", encryptionKey)
                .map((row, metadata) -> Reservation.builder()
                    .id(row.get("id", Long.class))
                    .userId(row.get("user_id", Long.class))
                    .postId(row.get("post_id", Long.class))
                    .makerId(row.get("maker_id", Long.class))
                    .minutesPrice(row.get("minute_price", Integer.class))
                    .minutes(row.get("minutes", Integer.class))
                    .person(row.get("person", Integer.class))
                    .personPrice(row.get("person_price", Integer.class))
                    .cancelMessage(row.get("cancel_message", String.class))
                    .reserveLocation(row.get("reserve_location", String.class))
                    .createAt(row.get("created_at", LocalDateTime.class))
                    .reservationTime(row.get("reservation_time", LocalDateTime.class))
                    .email(row.get("email", String.class))
                    .phoneNumber(row.get("phone_number", String.class))
                    .build()
                )
                .all()
                .collectList()
                .map(reservations -> PageResult.<Reservation>builder()
                    .offset(offset)
                    .limit(limit)
                    .data(reservations)
                    .build())
        );
    }

    @Override
    public Mono<PageResult<Reservation>> findByMakerId(int limit, int offset, long makerId) {
        return getEncryptionKey().flatMap(encryptionKey ->
            databaseClient.sql("""
                    SELECT id, user_id, post_id, maker_id, minute_price, minutes, person, person_price, 
                           cancel_message, reserve_location, created_at, reservation_time,
                           pgp_sym_decrypt(email::bytea, :encryptionKey) AS email, 
                           pgp_sym_decrypt(phone_number::bytea, :encryptionKey) AS phone_number
                    FROM reservation 
                    WHERE maker_id = :makerId 
                    ORDER BY reservation_time DESC 
                    LIMIT :limit OFFSET :offset
                    """)
                .bind("makerId", makerId)
                .bind("limit", limit)
                .bind("offset", offset)
                .bind("encryptionKey", encryptionKey)
                .map((row, metadata) -> Reservation.builder()
                    .id(row.get("id", Long.class))
                    .userId(row.get("user_id", Long.class))
                    .postId(row.get("post_id", Long.class))
                    .makerId(row.get("maker_id", Long.class))
                    .minutesPrice(row.get("minute_price", Integer.class))
                    .minutes(row.get("minutes", Integer.class))
                    .person(row.get("person", Integer.class))
                    .personPrice(row.get("person_price", Integer.class))
                    .cancelMessage(row.get("cancel_message", String.class))
                    .reserveLocation(row.get("reserve_location", String.class))
                    .createAt(row.get("created_at", LocalDateTime.class))
                    .reservationTime(row.get("reservation_time", LocalDateTime.class))
                    .email(row.get("email", String.class))
                    .phoneNumber(row.get("phone_number", String.class))
                    .build()
                )
                .all()
                .collectList()
                .map(reservations -> PageResult.<Reservation>builder()
                    .offset(offset)
                    .limit(limit)
                    .data(reservations)
                    .build())
        );
    }

    @Override
    public Mono<Reservation> findById(long id) {
        return getEncryptionKey().flatMap(encryptionKey ->
            databaseClient.sql("""
                    SELECT id, user_id, post_id, maker_id, minute_price, minutes, person, person_price, 
                           cancel_message, reserve_location, created_at, reservation_time,
                           pgp_sym_decrypt(email::bytea, :encryptionKey) AS email, 
                           pgp_sym_decrypt(phone_number::bytea, :encryptionKey) AS phone_number
                    FROM reservation 
                    WHERE id = :id
                    """)
                .bind("id", id)
                .bind("encryptionKey", encryptionKey)
                .map((row, metadata) -> Reservation.builder()
                    .id(row.get("id", Long.class))
                    .userId(row.get("user_id", Long.class))
                    .postId(row.get("post_id", Long.class))
                    .makerId(row.get("maker_id", Long.class))
                    .minutesPrice(row.get("minute_price", Integer.class))
                    .minutes(row.get("minutes", Integer.class))
                    .person(row.get("person", Integer.class))
                    .personPrice(row.get("person_price", Integer.class))
                    .cancelMessage(row.get("cancel_message", String.class))
                    .reserveLocation(row.get("reserve_location", String.class))
                    .createAt(row.get("created_at", LocalDateTime.class))
                    .reservationTime(row.get("reservation_time", LocalDateTime.class))
                    .email(row.get("email", String.class))
                    .phoneNumber(row.get("phone_number", String.class))
                    .build()
                )
                .one()
        );
    }

    @Override
    public Mono<Integer> countByUserId(long userId) {
        return databaseClient.sql("""
                SELECT COUNT(*)
                FROM reservation
                WHERE user_id = :userId
                """)
            .bind("userId", userId)
            .map((row, rowMetadata) -> row.get(0, Integer.class))  // 결과를 Long 타입으로 매핑
            .one();  // 결과를 Mono<Long>으로 반환
    }

    @Override
    public Mono<Boolean> cancel(long id, String cancelMessage) {
        return databaseClient.sql("""
                UPDATE reservation SET cancel_message = :cancelMessage
                WHERE id = :id
                """)
            .bind("cancelMessage", cancelMessage)
            .bind("id", id)
            .map((row, rowMetadata) -> true)
            .one();
    }

    @Override
    public Mono<Boolean> findMakerPendingReservation(Long makerId) {
        LocalDateTime currentTime = LocalDateTime.now(ZoneId.of("Asia/Tokyo"));
        return databaseClient.sql("""
                SELECT EXISTS(SELECT 1 FROM reservation 
                WHERE maker_id = :makerId AND reservation_time > :currentTime)
                """)
            .bind("makerId", makerId)
            .bind("currentTime", currentTime)
            .map((row, rowMetadata) -> row.get(0, Boolean.class))
            .one();
    }

    @Override
    public Mono<Boolean> findUserPendingReservation(Long userId) {
        LocalDateTime currentTime = LocalDateTime.now(ZoneId.of("Asia/Tokyo"));
        return databaseClient.sql("""
                SELECT EXISTS(SELECT 1 FROM reservation 
                WHERE user_id = :userId AND reservation_time > :currentTime)
                """)
            .bind("userId", userId)
            .bind("currentTime", currentTime)
            .map((row, rowMetadata) -> row.get(0, Boolean.class))
            .one();
    }
}
