package com.example.demo.service;

import com.example.demo.dto.ReservationResponseDto;
import com.example.demo.entity.*;
import com.example.demo.exception.ReservationConflictException;
import com.example.demo.repository.ItemRepository;
import com.example.demo.repository.ReservationRepository;
import com.example.demo.repository.UserRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.FetchType;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final RentalLogService rentalLogService;

    public ReservationService(ReservationRepository reservationRepository,
                              ItemRepository itemRepository,
                              UserRepository userRepository,
                              RentalLogService rentalLogService,
                              EntityManager em) {
        this.reservationRepository = reservationRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.rentalLogService = rentalLogService;
        this.em = em;
    }

    // TODO: 1. 트랜잭션 이해
    /**
     * @Transactional 을 적용하여 메소드 전체를 하나의 로직으로 구성합니다.
     * @Transactional 이 적용된 메소드 내에서 예외가 발생한다면 이미 정상 실행 되었던 부분을 실행하기 전으로 되돌리는 작업을 실행합니다.
     * 즉, 하나의 예외라도 발생한다면 이 메소드를 실행하기 전과 같은 상태로 돌아갑니다.
     * All or Nothing
     */
    @Transactional
    public void createReservation(Long itemId, Long userId, LocalDateTime startAt, LocalDateTime endAt) {
        // 쉽게 데이터를 생성하려면 아래 유효성검사 주석 처리
        List<Reservation> haveReservations = reservationRepository.findConflictingReservations(itemId, startAt, endAt);
        if(!haveReservations.isEmpty()) {
            throw new ReservationConflictException("해당 물건은 이미 그 시간에 예약이 있습니다.");
        }

        Item item = itemRepository.findById(itemId).orElseThrow(() -> new IllegalArgumentException("해당 ID에 맞는 값이 존재하지 않습니다."));
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("해당 ID에 맞는 값이 존재하지 않습니다."));
        Reservation reservation = new Reservation(item, user, "PENDING", startAt, endAt);
        Reservation savedReservation = reservationRepository.save(reservation);

        RentalLog rentalLog = new RentalLog(savedReservation, "로그 메세지", "CREATE");
        rentalLogService.save(rentalLog); // 여기서 NULL 발생
    }

    // TODO: 3. N+1 문제
    /**
     * 1. 엔티티의 @ManyToOne의 fetch 속성에 FetchType.LAZY 속성을 설정해줍니다. 기본은 FetchType.EAGER
     * 2. findAll 메소드에 jqpl 쿼리를 직접 JOIN FETCH 속성을 넣어 줍니다.
     * @Query("SELECT r " +
     *             "FROM Reservation r " +
     *             "JOIN FETCH r.user " +
     *             "JOIN FETCH r.item")
     * List<Reservation> findAll();
     */
    public List<ReservationResponseDto> getReservations() {
        List<Reservation> reservations = reservationRepository.findAll();

        return reservations.stream().map(reservation -> {
            User user = reservation.getUser();
            Item item = reservation.getItem();

            return new ReservationResponseDto(
                    reservation.getId(),
                    user.getNickname(),
                    item.getName(),
                    reservation.getStartAt(),
                    reservation.getEndAt()
            );
        }).toList();
    }

    // TODO: 5. QueryDSL 검색 개선
    /**
     * EntityManager 를 선언하고
     * QReservation 을 선언하여
     * 쿼리 DSL 을 작성해줍니다.
     */
    @PersistenceContext
    private final EntityManager em;
    public List<ReservationResponseDto> searchAndConvertReservations(Long userId, Long itemId) {
//        List<Reservation> reservations = searchReservations(userId, itemId);
        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(em);
        QReservation reservation = QReservation.reservation;
        List<Reservation> reservations = jpaQueryFactory
                .selectFrom(reservation)
                .where(reservation.user.id.eq(userId)
                        .and(reservation.item.id.eq(itemId)))
                .fetch();

        return convertToDto(reservations);
    }

    public List<Reservation> searchReservations(Long userId, Long itemId) {

        if (userId != null && itemId != null) {
            return reservationRepository.findByUserIdAndItemId(userId, itemId);
        } else if (userId != null) {
            return reservationRepository.findByUserId(userId);
        } else if (itemId != null) {
            return reservationRepository.findByItemId(itemId);
        } else {
            return reservationRepository.findAll();
        }
    }

    private List<ReservationResponseDto> convertToDto(List<Reservation> reservations) {
        return reservations.stream()
                .map(reservation -> new ReservationResponseDto(
                        reservation.getId(),
                        reservation.getUser().getNickname(),
                        reservation.getItem().getName(),
                        reservation.getStartAt(),
                        reservation.getEndAt()
                ))
                .toList();
    }

    // TODO: 7. 리팩토링
    // PENDING, APPROVED, CANCELED, EXPIRED
    @Transactional
    public void updateReservationStatus(Long reservationId, String status) {
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(() -> new IllegalArgumentException("해당 ID에 맞는 데이터가 존재하지 않습니다."));

        if ("APPROVED".equals(status)) {
            if (!"PENDING".equals(reservation.getStatus())) {
                throw new IllegalArgumentException("PENDING 상태만 APPROVED로 변경 가능합니다.");
            }
            reservation.updateStatus("APPROVED");
        } else if ("CANCELED".equals(status)) {
            if ("EXPIRED".equals(reservation.getStatus())) {
                throw new IllegalArgumentException("EXPIRED 상태인 예약은 취소할 수 없습니다.");
            }
            reservation.updateStatus("CANCELED");
        } else if ("EXPIRED".equals(status)) {
            if (!"PENDING".equals(reservation.getStatus())) {
                throw new IllegalArgumentException("PENDING 상태만 EXPIRED로 변경 가능합니다.");
            }
            reservation.updateStatus("EXPIRED");
        } else {
            throw new IllegalArgumentException("올바르지 않은 상태: " + status);
        }
    }
}
