package com.pickleball.springdemo.consumer;

import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.util.Map;

@ConditionalOnProperty(prefix = "kafka", name = "enabled", havingValue = "true")
@Component
@Profile("kafka")
public class UserEventsConsumer {

    @KafkaListener(
            topics = "user-events",
            groupId = "user-events-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeUserEvent(
            @Payload Map<String, Object> message,
            @Header(KafkaHeaders.RECEIVED_KEY) Map<String, Object> key,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment ack
    ) {
        try {
            System.out.println("=".repeat(50));
            System.out.println("Received message:");
            System.out.println("Key: " + key);
            System.out.println("Value: " + message);
            System.out.println("Partition: " + partition);
            System.out.println("Offset: " + offset);
            System.out.println("-----------------------------------");

            // Process your message here
            processUserEvent(message);

            // Manually acknowledge the message
            ack.acknowledge();
            System.out.println("Message acknowledged successfully");

        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
            e.printStackTrace();
            // Don't acknowledge so the message can be retried
        }
    }

    private void processUserEvent(Map<String, Object> event) {
        try {
            String action = (String) event.get("action");
            Integer userId = (Integer) event.get("user_id");

            System.out.println("Processing event for user " + userId + ", action: " + action);

            switch (action) {
                case "login":
                    System.out.println("✅ User " + userId + " logged in");
                    break;
                case "logout":
                    System.out.println("✅ User " + userId + " logged out");
                    break;
                case "purchase":
                    Double amount = (Double) event.get("amount");
                    System.out.println("✅ User " + userId + " made a purchase of $" + amount);
                    break;
                case "view_item":
                    String itemId = (String) event.get("item_id");
                    System.out.println("✅ User " + userId + " viewed item: " + itemId);
                    break;
                case "add_to_cart":
                    String cartItemId = (String) event.get("item_id");
                    System.out.println("✅ User " + userId + " added item to cart: " + cartItemId);
                    break;
                default:
                    System.out.println("❓ Unknown action: " + action);
            }

            // Print all event properties for debugging
            System.out.println("All event properties:");
            event.forEach((k, v) -> System.out.println("  " + k + ": " + v));

        } catch (Exception e) {
            System.err.println("Error in processUserEvent: " + e.getMessage());
            throw e; // Re-throw to trigger error handling
        }
    }
}