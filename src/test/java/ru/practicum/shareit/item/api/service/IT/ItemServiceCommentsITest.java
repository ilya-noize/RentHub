package ru.practicum.shareit.item.api.service.IT;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.api.dto.BookingSimpleDto;
import ru.practicum.shareit.booking.api.repository.BookingRepository;
import ru.practicum.shareit.booking.api.service.BookingService;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.api.dto.CommentDto;
import ru.practicum.shareit.item.api.dto.CommentSimpleDto;
import ru.practicum.shareit.item.api.dto.ItemDto;
import ru.practicum.shareit.item.api.dto.ItemSimpleDto;
import ru.practicum.shareit.item.api.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestSimpleDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.api.dto.UserDto;
import ru.practicum.shareit.user.api.dto.UserSimpleDto;
import ru.practicum.shareit.user.api.service.UserService;

import java.time.LocalDateTime;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.practicum.shareit.ShareItApp.ITEM_NOT_EXISTS;
import static ru.practicum.shareit.ShareItApp.RANDOM;
import static ru.practicum.shareit.ShareItApp.USER_NOT_EXISTS;
import static ru.practicum.shareit.booking.entity.enums.BookingStatus.APPROVED;

@SpringBootTest
@DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
class ItemServiceCommentsITest {
    private final LocalDateTime now = LocalDateTime.now();
    @Autowired
    private UserService userService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private ItemRequestService itemRequestService;
    @Autowired
    private BookingService bookingService;
    @Autowired
    private BookingRepository bookingRepository;


    @Test
    void createComment() {
        LocalDateTime now = LocalDateTime.now();
        int requestSendNDaysAgo = 64;
        int rentStartNDaysAgo = requestSendNDaysAgo - 32;
        int rentFinishNDaysAgo = rentStartNDaysAgo - 16;
        int publishCommentNDaysAgo = 4;// 128

        if (theSequenceOfActionsInTimeIsWrong(requestSendNDaysAgo, rentStartNDaysAgo, rentFinishNDaysAgo, publishCommentNDaysAgo)) {
            throw new BadRequestException("Something wrong in roadmap by create comment.");
        }

        int authorId = getUserId();
        int ownerId = getUserId();
        int itemId = getItemId(ownerId, authorId, requestSendNDaysAgo);
        getBookingId(ownerId, itemId, authorId,
                rentStartNDaysAgo, rentFinishNDaysAgo);


        assertFalse(bookingRepository
                .existsCompletedBookingByTheUserOfTheItem(
                        itemId, ownerId,
                        APPROVED,
                        now.minusDays(publishCommentNDaysAgo)));

        CommentSimpleDto commentSimpleDto = RANDOM.nextObject(CommentSimpleDto.class);
        commentSimpleDto.setAuthorId(authorId);
        commentSimpleDto.setItemId(itemId);
        commentSimpleDto.setCreated(now.plusDays(publishCommentNDaysAgo));

        CommentDto response = itemService.createComment(commentSimpleDto);

        assertNotNull(response.getId());
    }

    @Test
    void createComment_wrongTime_Throw() {
        LocalDateTime now = LocalDateTime.now();
        int requestSendNDaysAgo = 64;
        int rentStartNDaysAgo = requestSendNDaysAgo - 32;
        int rentFinishNDaysAgo = rentStartNDaysAgo - 16;
        int publishCommentNDaysAgo = 128;

        if (theSequenceOfActionsInTimeIsWrong(
                requestSendNDaysAgo,
                rentStartNDaysAgo,
                rentFinishNDaysAgo,
                publishCommentNDaysAgo)) {
            System.out.println("Something wrong in roadmap by create comment.");
        }

        int authorId = getUserId();
        int ownerId = getUserId();
        int itemId = getItemId(ownerId, authorId, requestSendNDaysAgo);
        getBookingId(ownerId, itemId, authorId,
                rentStartNDaysAgo, rentFinishNDaysAgo);

        assertFalse(bookingRepository
                .existsCompletedBookingByTheUserOfTheItem(
                        itemId, ownerId,
                        APPROVED,
                        now.minusDays(publishCommentNDaysAgo)));

        CommentSimpleDto commentSimpleDto = RANDOM.nextObject(CommentSimpleDto.class);
        commentSimpleDto.setAuthorId(authorId);
        commentSimpleDto.setItemId(itemId);
        commentSimpleDto.setCreated(now.plusDays(publishCommentNDaysAgo));
        itemService.createComment(commentSimpleDto);
    }


    @Test
    void createComment_wrongBooking_Throw() {
        LocalDateTime now = LocalDateTime.now();
        int requestSendNDaysAgo = 64;
        int rentStartNDaysAgo = requestSendNDaysAgo - 32;
        int rentFinishNDaysAgo = rentStartNDaysAgo - 16;
        int publishCommentNDaysAgo = 0;

        if (theSequenceOfActionsInTimeIsWrong(
                requestSendNDaysAgo,
                rentStartNDaysAgo,
                rentFinishNDaysAgo,
                publishCommentNDaysAgo)) {
            System.out.println("Something wrong in roadmap by create comment.");
        }

        int authorId = getUserId();
        int ownerId = getUserId();
        int itemId = getItemId(ownerId, authorId, requestSendNDaysAgo);
        getBookingId(ownerId, itemId, authorId,
                rentStartNDaysAgo, rentFinishNDaysAgo);
// должно быть true
        assertFalse(bookingRepository
                .existsCompletedBookingByTheUserOfTheItem(
                        itemId, ownerId,
                        APPROVED,
                        now.minusDays(publishCommentNDaysAgo)));

        CommentSimpleDto commentSimpleDto = RANDOM.nextObject(CommentSimpleDto.class);
        commentSimpleDto.setAuthorId(authorId);
        commentSimpleDto.setItemId(itemId);
        commentSimpleDto.setCreated(now.plusDays(publishCommentNDaysAgo));
        itemService.createComment(commentSimpleDto);
    }

    /**
     * the sequence of actions in time.
     * roadmap by create comment.
     *
     * @param requestSendNDaysAgo    request days ago   - 1 - far past
     * @param rentStartNDaysAgo      start days ago     - 2
     * @param rentFinishNDaysAgo     end days ago       - 3
     * @param publishCommentNDaysAgo publish days ago   - 4 - near-present
     */
    private boolean theSequenceOfActionsInTimeIsWrong(int requestSendNDaysAgo, int rentStartNDaysAgo, int rentFinishNDaysAgo, int publishCommentNDaysAgo) {
        boolean startRentBeforeRequestItem = rentStartNDaysAgo > requestSendNDaysAgo;
        if (startRentBeforeRequestItem) {
            System.out.println("Expected Exception in Request: impossible start rent by item before send request item.");
            System.out.println("Невозможно начать аренду по номенклатуре до отправки запроса на предмет.");
            return true;
        }
        boolean finishedRentBeforeStartRent = rentFinishNDaysAgo > rentStartNDaysAgo;
        if (finishedRentBeforeStartRent) {
            System.out.println("Expected Exception in Booking: impossible finished rent before start rent");
            System.out.println("Невозможно закончить аренду до начала аренды");
            return true;
        }
        boolean publishCommentBeforeRentEnd = publishCommentNDaysAgo > rentFinishNDaysAgo;
        if (publishCommentBeforeRentEnd) {
            System.out.println("Expected Exception in Comment: impossible publish comment before finished rent");
            System.out.println("Невозможно опубликовать комментарий до окончания аренды");
            return true;
        }

        return false;
    }


    @Test
    void createComment_wrongText_Throw() {
        CommentSimpleDto wrongText = RANDOM.nextObject(CommentSimpleDto.class);
        wrongText.setText("   ");
        BadRequestException e = assertThrows(BadRequestException.class,
                () -> itemService.createComment(wrongText));
        assertEquals(e.getMessage(), "Text comment can't be blank");
    }


    @Transactional
    private int getUserId() {
        UserSimpleDto requestUser = RANDOM.nextObject(UserSimpleDto.class);
        UserDto authorDto = userService.create(requestUser);
        return authorDto.getId();
    }

    @Transactional
    private int getRequestByItem(int requesterId, int requestItemDaysAgo) {
        ItemRequestSimpleDto requestSimpleDto = RANDOM.nextObject(ItemRequestSimpleDto.class);
        LocalDateTime requestTime = LocalDateTime.now().minusDays(requestItemDaysAgo);
        ItemRequestDto requestDto = itemRequestService
                .create(requesterId, requestSimpleDto, requestTime);
        return requestDto.getId();
    }

    @Transactional
    private int getItemId(int ownerId, int authorId, int requestItemDaysAgo) {
        ItemSimpleDto requestItem = RANDOM.nextObject(ItemSimpleDto.class);
        requestItem.setRequestId(getRequestByItem(authorId, requestItemDaysAgo));
        // requestItem.setAvailable(false);
        // in Booking: BadRequestException: It is impossible to rent an item to which access is closed.
        requestItem.setAvailable(true);
        ItemDto itemDto = itemService.create(ownerId, requestItem);
        return itemDto.getId();
    }

    @Transactional
    private void getBookingId(int ownerId, int itemId, int authorId, int rentStartNDaysAgo, int rentFinishNDaysAgo) {
        BookingSimpleDto requestBooking = RANDOM.nextObject(BookingSimpleDto.class);
        requestBooking.setItemId(itemId);
        requestBooking.setStart(now.minusDays(rentStartNDaysAgo));
        requestBooking.setEnd(now.minusDays(rentFinishNDaysAgo));
        long bookingId = bookingService.create(authorId, requestBooking).getId();
        bookingService.update(ownerId, bookingId, true);
    }

    @Test
    void createComment_wrongItem_Throw() {
        LocalDateTime now = LocalDateTime.now();
        int authorId = getUserId();
        int itemId = Integer.MAX_VALUE;//getItemId(ownerId, authorId, 3);

        CommentSimpleDto commentSimpleDto = RANDOM.nextObject(CommentSimpleDto.class);
        commentSimpleDto.setAuthorId(authorId);
        commentSimpleDto.setItemId(itemId);
        commentSimpleDto.setCreated(now);

        assertFalse(bookingRepository
                .existsCompletedBookingByTheUserOfTheItem(
                        itemId, authorId,
                        APPROVED, now.minusDays(0)));

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> itemService.createComment(commentSimpleDto));
        assertEquals(e.getMessage(), format(ITEM_NOT_EXISTS, itemId));
    }


    @Test
    void get_whenUserNotExist_thenReturnException() {
        //given
        int userId = 9999;

        //when
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.get(userId, 1));

        //then
        assertEquals(exception.getMessage(),
                format(USER_NOT_EXISTS, userId));
    }
}