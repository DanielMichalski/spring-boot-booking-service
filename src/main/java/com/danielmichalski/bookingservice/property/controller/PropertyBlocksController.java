package com.danielmichalski.bookingservice.property.controller;

import static org.springframework.http.HttpStatus.NO_CONTENT;

import com.danielmichalski.bookingservice.property.dto.BlockPropertyRequest;
import com.danielmichalski.bookingservice.property.dto.PropertyBlockDto;
import com.danielmichalski.bookingservice.property.dto.UpdateBlockRequest;
import com.danielmichalski.bookingservice.property.service.PropertyBlocksService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/properties/{propertyId}/blocks")
@RequiredArgsConstructor
public class PropertyBlocksController {

  private final PropertyBlocksService propertyBlocksService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public PropertyBlockDto blockProperty(@PathVariable UUID propertyId,
                                        @Valid @RequestBody BlockPropertyRequest request) {
    return propertyBlocksService.blockProperty(propertyId, request);
  }

  @PutMapping("/{blockId}")
  @ResponseStatus(NO_CONTENT)
  public void updateBlock(@PathVariable UUID propertyId,
                          @PathVariable UUID blockId,
                          @Valid @RequestBody UpdateBlockRequest request) {
    propertyBlocksService.updateBlock(propertyId, blockId, request);
  }

  @DeleteMapping("/{blockId}")
  @ResponseStatus(NO_CONTENT)
  public void cancelBlock(@PathVariable UUID propertyId, @PathVariable UUID blockId) {
    propertyBlocksService.cancelBlock(propertyId, blockId);
  }

}
