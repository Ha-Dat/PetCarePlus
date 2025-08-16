package org.example.petcareplus.controller;

import jakarta.servlet.http.HttpSession;
import org.example.petcareplus.dto.ChatMessageDTO;
import org.example.petcareplus.entity.Account;
import org.example.petcareplus.enums.AccountRole;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class ChatController {

    // gửi tin nhắn trong từng room
    @MessageMapping("/chat/{roomId}")
    @SendTo("/topic/chat/{roomId}")
    public ChatMessageDTO sendMessage(
            @DestinationVariable String roomId,
            ChatMessageDTO message,
            @AuthenticationPrincipal Account currentAccount
    ) {
        System.out.println("Broadcasting to room: " + roomId); // ✅ Log room
        System.out.println("Message: " + message.getContent()); // ✅ Log nội dung

        // phân quyền: nếu là customer -> chỉ được chat trong phòng của chính họ
        if (currentAccount.getRole() == AccountRole.CUSTOMER) {
            String expectedRoomId = "chatroom-" + currentAccount.getAccountId();
            if (!expectedRoomId.equals(roomId)) {
                throw new RuntimeException("Không có quyền truy cập phòng này");
            }
        }
        // staff thì được phép join bất kỳ room nào
        return message;
    }

    @GetMapping("chat")
    public String chat(
            HttpSession session,
            Model model,
            @RequestParam(required = false) Long customerId
    ) {
        Account account = (Account) session.getAttribute("loggedInUser");
        if (account == null) return "redirect:/login";

        model.addAttribute("name", account.getName());

        // Xử lý roomId
        if (account.getRole() == AccountRole.CUSTOMER) {
            model.addAttribute("roomId", "chatroom-" + account.getAccountId());
        }
        else if (customerId != null) {
            // Sửa thành customerId thay vì account.getAccountId()
            model.addAttribute("roomId", "chatroom-" + customerId);
        }
        else {
            return "redirect:/staff-dashboard"; // Redirect đến trang chọn customer
        }

        // Thêm accountId để dùng trong hidden input
        model.addAttribute("accountId", account.getAccountId());

        return "chat";
    }
}
