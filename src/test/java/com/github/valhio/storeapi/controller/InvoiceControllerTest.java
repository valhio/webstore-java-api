package com.github.valhio.storeapi.controller;

import com.github.valhio.storeapi.config.SecurityConfiguration;
import com.github.valhio.storeapi.enumeration.Role;
import com.github.valhio.storeapi.exception.domain.InvoiceNotFoundException;
import com.github.valhio.storeapi.filter.JWTAccessDeniedHandler;
import com.github.valhio.storeapi.filter.JWTAuthenticationEntryPoint;
import com.github.valhio.storeapi.filter.JWTAuthorizationFilter;
import com.github.valhio.storeapi.model.User;
import com.github.valhio.storeapi.model.UserPrincipal;
import com.github.valhio.storeapi.utility.JWTTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import({SecurityConfiguration.class, JWTAuthorizationFilter.class, JWTAccessDeniedHandler.class, JWTAuthenticationEntryPoint.class})
@WebMvcTest(InvoiceController.class)
@AutoConfigureMockMvc
class InvoiceControllerTest {


    @MockBean
    private InvoiceService invoiceService;
    @MockBean
    private JWTTokenProvider jwtTokenProvider;
    // This is required for the tests to run, otherwise it will throw a stack overflow error.
    // Spring Security requires an AuthenticationManager to be present in the context.
    // Apparently, it is a bug in Spring Framework, and it will(should) be fixed in the next release(v5.3.24). Current version is 5.3.23.
    @MockBean
    private AuthenticationManager authenticationManager;
    @Autowired
    private MockMvc mockMvc;

    @Nested
    @DisplayName("Get invoice by invoice number")
    class GetInvoiceByInvoiceNumber {

        @Test
        @DisplayName("Should return invoice when auth user's email is the same as the invoice's user email")
        void shouldReturnInvoiceWhenInvoiceNumberIsValid() throws Exception {
            User user = new User();
            user.setEmail("foo@bar");
            user.setRole(Role.ROLE_USER);
            user.setAuthorities(Role.ROLE_USER.getAuthorities());
            UserPrincipal authUser = new UserPrincipal(user);

            Invoice invoice = new Invoice();
            invoice.setUser(user);

            when(invoiceService.findByInvoiceNumber(anyString())).thenReturn(invoice);

            mockMvc.perform(get("/api/v1/invoice/1234567890")
                            .with(user(authUser)))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should return 401 when auth user's email is not the same as the invoice's user email")
        void shouldReturn401WhenInvoiceNumberIsValid() throws Exception {
            User user = new User();
            user.setEmail("foo@bar");
            user.setRole(Role.ROLE_USER);
            user.setAuthorities(Role.ROLE_USER.getAuthorities());
            UserPrincipal authUser = new UserPrincipal(user);

            User user2 = new User();
            user2.setEmail("something@else");
            user2.setRole(Role.ROLE_USER);
            user2.setAuthorities(Role.ROLE_USER.getAuthorities());
            UserPrincipal authUser2 = new UserPrincipal(user2);

            Invoice invoice = new Invoice();
            invoice.setUser(user);

            when(invoiceService.findByInvoiceNumber(anyString())).thenReturn(invoice);

            mockMvc.perform(get("/api/v1/invoice/1234567890")
                            .with(user(authUser2)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Should return 403 when auth user is not logged in")
        void shouldReturn403WhenAuthUserIsNotLoggedIn() throws Exception {
            mockMvc.perform(get("/api/v1/invoice/1234567890"))
                    .andExpect(status().isForbidden());
        }


        @Test
        @DisplayName("Should return 404 when invoice number is not found")
        @WithMockUser(username = "foo@bar", roles = "USER")
        void shouldReturn404WhenInvoiceNumberIsNotFound() throws Exception {
            when(invoiceService.findByInvoiceNumber(anyString())).thenThrow(InvoiceNotFoundException.class);
            mockMvc.perform(get("/api/v1/invoice/1234567890"))
                    .andExpect(status().isNotFound());
        }
    }

}