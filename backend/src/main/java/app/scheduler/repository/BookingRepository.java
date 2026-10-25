package app.scheduler.repository;

import app.scheduler.domain.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("select b from Booking b join fetch b.slot s order by s.date, s.startTime")
    List<Booking> findAllWithSlot();

    @Query("select b from Booking b join fetch b.slot where b.id = :id")
    Optional<Booking> findByIdWithSlot(@Param("id") Long id);
}
