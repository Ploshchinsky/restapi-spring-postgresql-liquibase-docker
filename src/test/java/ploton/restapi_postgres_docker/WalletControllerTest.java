package ploton.restapi_postgres_docker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ploton.restapi_postgres_docker.controller.WalletController;
import ploton.restapi_postgres_docker.model.Wallet;
import ploton.restapi_postgres_docker.service.WalletService;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@AutoConfigureMockMvc
@WebMvcTest(WalletController.class)
class WalletControllerTest {


    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private WalletService walletService;

    private static Wallet wallet;

    @BeforeEach
    public void walletInit() {
        wallet = new Wallet();
        wallet.setId(UUID.fromString("33aaf4c9-0a49-4d49-860e-90d1cdca7e22"));
        wallet.setBalance(BigDecimal.valueOf(19320.00));
    }

    @Test
    void restapi_getBalance() throws Exception {
        UUID id = wallet.getId();
        BigDecimal expected = BigDecimal.valueOf(19320.00);

        Mockito.when(this.walletService.getBalance(wallet.getId())).thenReturn(wallet.getBalance());
        mockMvc.perform(get("/api/v1/wallets/{walletID}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(expected.toString()));
    }

    @Test
    void restapi_walletAction_withdrawACCEPTED() throws Exception {
        UUID id = wallet.getId();
        BigDecimal amount = BigDecimal.valueOf(160.00);
        String operationType = "WITHDRAW";

        UUID expectedUUID = UUID.fromString("33aaf4c9-0a49-4d49-860e-90d1cdca7e22");
        BigDecimal expectedBalance = BigDecimal.valueOf(19160.00);

        // Check that the operation passes and returns the correct UUID
        Mockito.doNothing().when(walletService).walletAction(id, operationType, amount);
        mockMvc.perform(post("/api/v1/wallets")
                        .param("walletID", id.toString())
                        .param("operationType", operationType)
                        .param("amount", amount.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$").value(expectedUUID.toString()));

        // Checking the balance after the operation
        Mockito.when(walletService.getBalance(id)).thenReturn(expectedBalance);
        mockMvc.perform(get("/api/v1/wallets/{walletID}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(expectedBalance.toString()));
    }
    @Test
    void restapi_walletAction_depositACCEPTED() throws Exception {
        UUID id = wallet.getId();
        BigDecimal amount = BigDecimal.valueOf(180.00);
        String operationType = "DEPOSIT";

        UUID expectedUUID = UUID.fromString("33aaf4c9-0a49-4d49-860e-90d1cdca7e22");
        BigDecimal expectedBalance = BigDecimal.valueOf(19500.00);

        // Check that the operation passes and returns the correct UUID
        Mockito.doNothing().when(walletService).walletAction(id, operationType, amount);
        mockMvc.perform(post("/api/v1/wallets")
                        .param("walletID", id.toString())
                        .param("operationType", operationType)
                        .param("amount", amount.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$").value(expectedUUID.toString()));

        // Checking the balance after the operation
        Mockito.when(walletService.getBalance(id)).thenReturn(expectedBalance);
        mockMvc.perform(get("/api/v1/wallets/{walletID}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(expectedBalance.toString()));
    }

    @Test
    void restapi_walletAction_NOTENOUGHCASH() throws Exception {
        UUID id = wallet.getId();
        BigDecimal amount = BigDecimal.valueOf(19_500.00);
        String operationType = "WITHDRAW";

        Mockito.doNothing().when(walletService).walletAction(id, operationType, amount);
        mockMvc.perform(post("/api/v1/wallets"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void restapi_walletAction_WRONGID() throws Exception {
        UUID wrongId = UUID.fromString("33aaf4c9-0a49-4d49-860e-90d1cdca7e20");
        BigDecimal amount = BigDecimal.valueOf(19_500.00);
        String operationType = "WITHDRAW";

        Mockito.doNothing().when(walletService).walletAction(wrongId, operationType, amount);
        mockMvc.perform(post("/api/v1/wallets"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void restapi_walletAction_WRONOGTYPE() throws Exception {
        UUID id = wallet.getId();
        BigDecimal amount = BigDecimal.valueOf(19_500.00);
        String wrongType = "WRONG";

        Mockito.doNothing().when(walletService).walletAction(id, wrongType, amount);
        mockMvc.perform(post("/api/v1/wallets"))
                .andExpect(status().isBadRequest());
    }
}
