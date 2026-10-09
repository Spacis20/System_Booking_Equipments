package project.example.reservation.system.reservations;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {

//    @Query("select r from ReservationEntity r where r.status = :status")
//    List<ReservationEntity> findAllByStatusIs(ReservationStatus status);
//
//    @Transactional
//    @Modifying
//    @Query("""
//        update ReservationEntity r
//        set r.userId = :userId,
//        r.roomId = :roomId,
//        r.startdate = :startdate,
//        r.endDate = :endDate,
//        r.status = :status
//        where r.id = :id""")
//    int updateAllFields(
//            @Param("userId") Long userId,
//            @Param("id") Long id,
//            @Param("roomId") Long roomId,
//            @Param("startDate") LocalDate startDate,
//            @Param("endDate") LocalDate endDate,
//            @Param("status") ReservationStatus status
//            );

    @Modifying
    @Query("update ReservationEntity r set r.status = :status where r.id = :id")
    void setStatus(@Param("id") Long id, @Param("status") ReservationStatus status);

    @Query("""
    SELECT r.id FROM ReservationEntity r
    WHERE r.room_id = :roomId
      AND :startDate < r.endDate
      AND r.startDate < :endDate
      AND r.status = :status
""")
    List<Long> findAllConflicts(
            @Param("roomId") Long roomId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("status") ReservationStatus status
    );

    @Query("""
    SELECT r FROM ReservationEntity r
    WHERE (:roomId IS NULL OR r.room_id = :roomId)
      AND (:userId IS NULL OR r.user_id = :userId)
""")
    List<ReservationEntity> searchAllByFilter(
            @Param("roomId") Long roomId,
            @Param("userId") Long userId,
            Pageable pageable
    );
}
