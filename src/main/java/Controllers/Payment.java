/*package Controllers;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.param.PaymentIntentCreateParams;

public class Payment {
    public void processPayment(int amount) {
        try {
            Stripe.apiKey = "";
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount((long) amount * 100) // Amount should be in cents
                    .setCurrency("usd")
                    .addPaymentMethodType("card")
                    .build();
            PaymentIntent intent = PaymentIntent.create(params);
            System.out.println("PaymentIntent created. Client Secret: " + intent.getClientSecret());
        } catch (StripeException e) {
            System.out.println("Payment failed. Error: " + e.getMessage());
        }
    }
}


*/
