package RestAssuredTests;

import com.simplebooks.api.api.BaseApi;
import com.simplebooks.api.payloads.StatusResponse;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class ApiTests extends BaseApi {

    @Test
    @DisplayName("Verify that Books endpoint returns a correct value for 'status' field")
    public void testGetBooks() {

        StatusResponse statusResponse =
                given()
                .when()
                .get(statusEndpoint)
                .then()
                .statusCode(200)
                .extract().as(StatusResponse.class);

        Assertions.assertEquals("OK", statusResponse.getStatus());
    }

    @Test
    @DisplayName("Verify that all books are returned in the response with correct 'name' field")
    public void testGetAllBooksEndpoint() {

        given().when()
                .get(booksEndpoint)
                .then()
                .statusCode(200)
                .assertThat()
                        .body("name", hasItems(
                                "The Russian", "Just as I Am", "The Vanishing Half", "The Midnight Library", "Untamed", "Viscount Who Loved Me"));
    }

    @Test
    @DisplayName("Verify query parameters 'limit' and 'type' work")
    public void checkGetAllBooksWithParameters() {

        given()
                .queryParam("limit", "1")
                .queryParam("type", "fiction")
                .when()
                .get(booksEndpoint)
                .then()
                .assertThat()
                .body("[0].type", equalTo("fiction"))
                .body("[1]", equalTo(null));
    }

    @Test
    @DisplayName("Verify path variable 'bookId' in books endpoint works")
    public void checkBookIdPathVariable() {

        given()
                .when()
                .get(booksEndpoint + "5")
                .then()
                .body("id", equalTo(5));
    }

    @Test
    public void checkGetAllOrders() {

        given()
                .headers(
                        "Authorization",
                        "Bearer " + bearerToken,
                        "Content-Type",
                        ContentType.JSON,
                        "Accept",
                        ContentType.JSON)
                .when()
                .get(ordersEndpoint)
                .then()
                .log().all()
                .assertThat()
                .statusCode(200);
    }

    @Test
    @DisplayName("Verify that using an invalid token is not possible")
    public void checkGetAllOrdersInvalidToken() {

        given()
                .headers(
                        "Authorization",
                        "Bearer " + "INVALID_TOKEN",
                        "Content-Type",
                        ContentType.JSON,
                        "Accept",
                        ContentType.JSON)
                .when()
                .get(ordersEndpoint)
                .then()
                .assertThat()
                .statusCode(401);
    }

    @Test
    @DisplayName("Verify create an order works")
    public void checkCreateOrder() {

        Map<String, Object> orderInfo = new HashMap<>();
        orderInfo.put("bookId", "1");
        orderInfo.put("customerName", "Testcho Testov");

        given()
                .headers(
                        "Authorization",
                        "Bearer " + bearerToken,
                        "Content-Type",
                        ContentType.JSON,
                        "Accept",
                        ContentType.JSON)
                .when()
                .body(orderInfo)
                .post(ordersEndpoint)
                .then()
                .statusCode(201)
                .body(
                    "created", is(true),
                    "orderId", notNullValue());
    }

    @Test
    @DisplayName("Create a new order, update the name of the customer and check if the change was successful")
    public void checkUpdateOrder() {

        String oldCustomerName = "Old Oldman";
        String newCustomerName = "New Newman";

        Map<String, Object> updateInfo = new HashMap<>();
        updateInfo.put("customerName", newCustomerName);

        Map<String, Object> orderInfo = new HashMap<>();
        orderInfo.put("bookId", "1");
        orderInfo.put("customerName", oldCustomerName);

        String orderId =
                given()
                .headers(
                        "Authorization",
                        "Bearer " + bearerToken,
                        "Content-Type",
                        ContentType.JSON,
                        "Accept",
                        ContentType.JSON)
                .when()
                .body(orderInfo)
                .post(ordersEndpoint)
                .then()
                .extract()
                .path("orderId");

        given()
                .headers(
                "Authorization",
                "Bearer " + bearerToken,
                "Content-Type",
                ContentType.JSON,
                "Accept",
                ContentType.JSON)
                .when()
                .body(updateInfo)
                .patch(ordersEndpoint + orderId)
                .then()
                .statusCode(204);

        given()
                .headers(
                        "Authorization",
                        "Bearer " + bearerToken,
                        "Content-Type",
                        ContentType.JSON,
                        "Accept",
                        ContentType.JSON)
                .when()
                .get(ordersEndpoint + orderId)
                .then()
                .body(
                        "customerName", is(newCustomerName)
                ).log().all();
    }

    //TODO: create tests for Delete order and API Authentication
}
