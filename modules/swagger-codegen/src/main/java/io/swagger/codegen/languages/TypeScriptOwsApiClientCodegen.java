package io.swagger.codegen.languages;

import io.swagger.codegen.*;
import io.swagger.models.properties.*;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

public class TypeScriptOwsApiClientCodegen extends TypeScriptFetchClientCodegen {
    private static final String DATATYPE_WITH_ENUM_SEPARATOR = "";

    public TypeScriptOwsApiClientCodegen() {
        super();
        addReservedWordLowerCase("url");
    }

    @Override
    protected void setOutputDirFields() {
        outputFolder = "generated-code/typescript-ows-api";
        embeddedTemplateDir = templateDir = "typescript-ows-api";
    }

    @Override
    public Map<String, Object> postProcessOperations(Map<String, Object> objs) {
        super.postProcessOperations(objs);
        if (objs != null) {
            Map<String, Object> operations = (Map<String, Object>) objs.get("operations");
            if (operations != null) {
                List<CodegenOperation> ops = (List<CodegenOperation>) operations.get("operation");
                for (CodegenOperation operation : ops) {
                    processOperation(operation);
                }
            }
        }
        return objs;
    }

    @Override
    public String getName() {
        return "typescript-ows-api";
    }

    @Override
    protected String getDatatypeWithEnumSeparator() {
        return DATATYPE_WITH_ENUM_SEPARATOR;
    }

    protected String sanitizeVVersionOperationId(String opId) {
        if (opId.indexOf("api") == 0) {
            // because lightning has a double route definition for /api/v1 and /v1
            // we can't strip api from it unless we want dupe definitions.
            if (opId.indexOf("Lightning") == -1 ) {
                opId = opId.replace("api", "");
            }
        }
        if (opId.indexOf("Api") == 0) {
            opId = opId.replace("Api", "");
        }
        if (opId.indexOf("vversion") > -1) {
            opId = opId.replace("vversion", "V1");
        }
        if (opId.indexOf("Vversion") > -1) {
            opId = opId.replace("Vversion", "V1");
        }
        if (opId.indexOf("V{version") > -1) {
            opId = opId.replace("V{version", "V1");
        }
        if (opId.indexOf("v{version") > -1) {
            opId = opId.replace("v{version", "V1");
        }
        return opId;
    }

    protected List<CodegenParameter> sanitizeOperationParameters(List<CodegenParameter> paramsToSanitize) {
        // repeat removal of unused version paramsToSanitize from query list
        List<CodegenParameter> sanitizedParams = new ArrayList<CodegenParameter>();
        for (CodegenParameter param : paramsToSanitize) {
            String paramName = param.paramName;
            if (!paramName.equalsIgnoreCase("apiVersion") && !paramName.equalsIgnoreCase("version")) {
                sanitizedParams.add(param);
            }
        }
        return sanitizedParams;
    }
    
    protected void processOperation(CodegenOperation operation) {
        // update method names & routes to include version info
        if (operation.path.indexOf("/api/beta") > -1) {
            operation.operationId = operation.operationId.replace("apiBeta", "Beta");
        } else if (operation.path.indexOf("/v{version}") > -1) {            
            operation.operationId = sanitizeVVersionOperationId(operation.operationId);
            operation.path = operation.path.replace("{version}", "1.0");
        }
        operation.nickname = operation.operationId;
        
        // remove now unused version info
        operation.allParams = sanitizeOperationParameters(operation.allParams);
        operation.queryParams = sanitizeOperationParameters(operation.queryParams);
        operation.pathParams = sanitizeOperationParameters(operation.pathParams);

        // add request object metadata
        for (CodegenParameter param : operation.allParams) {
            if (param.paramName.equalsIgnoreCase("request")) {
                operation.hasRequest = true;
            }
        }
    }
}
