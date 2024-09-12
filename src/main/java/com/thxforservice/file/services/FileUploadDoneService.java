package com.thxforservice.file.services;

import com.thxforservice.global.Utils;
import com.thxforservice.global.exceptions.BadRequestException;
import com.thxforservice.global.rests.ApiRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FileUploadDoneService {
    private final ApiRequest apiRequest;
    private final Utils utils;

    public void process(String gid) {
        ApiRequest result = apiRequest.request("/done/" + gid, "file-service");
        if (!result.getStatus().is2xxSuccessful()) {
            throw new BadRequestException(utils.getMessage("Fail.file.done"));
        }
    }
}