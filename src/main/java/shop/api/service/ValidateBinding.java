package shop.api.service;

import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.*;
import java.util.Map;

@Service
public class ValidateBinding {
      public Map<String,String> validate(BindingResult bindingResult){
          if(bindingResult.hasErrors()){
              Map<String,String> errorList=new HashMap<>();
              List<FieldError> fieldErrors = bindingResult.getFieldErrors();

              // Xử lý lỗi trường
              for (FieldError fieldError : fieldErrors) {
                  String field = fieldError.getField();
                  String errorMessage = fieldError.getDefaultMessage();
                  errorList.put(field,errorMessage);
              }

              return errorList;
          }
          return null;
      }
}
