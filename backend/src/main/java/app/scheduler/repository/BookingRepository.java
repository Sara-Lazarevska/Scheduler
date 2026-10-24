package app.scheduler.repository;

import app.scheduler.domain.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("select b from Booking b join fetch b.slot s order by s.date, s.startTime")
    List<Booking> findAllWithSlot();
}
