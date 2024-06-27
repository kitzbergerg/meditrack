package ase.meditrack.controller;

import ase.meditrack.exception.NotFoundException;
import ase.meditrack.service.PdfGenerationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.Month;
import java.time.Year;

@RestController
@RequestMapping("/api/pdf")
@Slf4j
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
public class PdfGenerationController {

    private final PdfGenerationService pdfGenerationService;

    public PdfGenerationController(PdfGenerationService pdfGenerationService) {
        this.pdfGenerationService = pdfGenerationService;
    }

    @GetMapping("")
    @PreAuthorize("hasAnyAuthority('SCOPE_admin', 'SCOPE_dm') || (hasAnyAuthority('SCOPE_employee'))")
    public ResponseEntity<byte[]> generatePdf(@RequestParam Year year, @RequestParam Month month, Principal principal) {
        try {
            byte[] pdfBytes = pdfGenerationService.generatePdf(principal, year, month);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "weekly_plan.pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);

        } catch (NotFoundException notFoundException) {
            return ResponseEntity.status(404).body(null);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
     }
}
