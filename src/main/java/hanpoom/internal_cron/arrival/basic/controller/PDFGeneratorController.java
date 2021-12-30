// package hanpoom.internal_cron.arrival.basic.controller;

// import javax.servlet.http.HttpSession;

// import hanpoom.internal_cron.arrival.basic.service.PDFGeneratorService;
// import hanpoom.internal_cron.misc.response.APIResponse;
// import hanpoom.internal_cron.misc.response.CommonResponse;
// import hanpoom.internal_cron.misc.response.ErrorResponse;

// import java.util.Base64;
// import java.util.HashMap;

// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.ResponseBody;
// import org.springframework.stereotype.Controller;

// @Controller
// public class PDFGeneratorController {
//     private static final String USER_ID = "user_id";
//     private PDFGeneratorService pdfGeneratorService;

//     public PDFGeneratorController(PDFGeneratorService pdfGeneratorService) {
//         this.pdfGeneratorService = pdfGeneratorService;
//     }

//     @ResponseBody
//     @GetMapping(value = "/arrival/validateLabel")
//     public APIResponse validateArrivalLabel(HttpSession session, @RequestParam String arrivalSeq) throws Exception {
//         String userId = (String) session.getAttribute(USER_ID);
//         if (userId != null) {
//             byte[] createdPdf = pdfGeneratorService.validatePDFArrivalLabel(arrivalSeq, userId);
//             if (createdPdf != null) {
//                 String encodedString = Base64.getEncoder().encodeToString(createdPdf);
//                 return new CommonResponse("200", encodedString);
//             } else {
//                 return new ErrorResponse("File Not Created.", "404");
//             }
//         } else {
//             return new ErrorResponse("Internal Server Error.", "500");
//         }
//     }

//     @ResponseBody
//     @PostMapping(value = "/arrival/validateMultipleLabels")
//     public APIResponse validateMultipleLabels(HttpSession session,
//             @RequestBody HashMap<String, Integer> arrivalSeqQty) {
//         String user_id = (String) session.getAttribute(USER_ID);
//         if (user_id != null) {
//             String base64String = pdfGeneratorService.validateMultiplePDFArrivalLabel(arrivalSeqQty, user_id);
//             if (base64String != null) {
//                 return new CommonResponse("200", base64String);
//             } else {
//                 return new ErrorResponse("File Not Created.", "404");
//             }
//         } else {
//             return new ErrorResponse("Session Expired", "403");
//         }
//     }
// }