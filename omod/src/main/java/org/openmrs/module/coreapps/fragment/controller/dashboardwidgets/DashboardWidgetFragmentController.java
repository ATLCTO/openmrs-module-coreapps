package org.openmrs.module.coreapps.fragment.controller.dashboardwidgets;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.InjectBeans;
import org.openmrs.ui.framework.fragment.FragmentConfiguration;

import java.io.IOException;
import java.util.Map;

public class DashboardWidgetFragmentController {

    public void controller(FragmentConfiguration config, @FragmentParam("app") AppDescriptor app, @InjectBeans PatientDomainWrapper patientWrapper) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        Object patient = null;
        patient = config.get("patient");
        if (patient == null ) {
            patient = config.get("patientId");
        }
        if (patient == null) {
            config.require("patient");
        }

        ObjectNode appConfig = app.getConfig();

        if (patient instanceof Patient) {
            patientWrapper.setPatient((Patient) patient);
        } else if (patient instanceof PatientDomainWrapper) {
            patientWrapper = (PatientDomainWrapper) patient;
        } else if (patient instanceof Integer) {
            // assume we have patientId
            patientWrapper.setPatient(Context.getPatientService().getPatient((Integer) patient));
        } else {
            throw new IllegalArgumentException("Patient must be of type Patient or PatientDomainWrapper");
        }
        appConfig.put("patientUuid", patientWrapper.getPatient().getUuid());
        Map<String, Object> appConfigMap = mapper.convertValue(appConfig, Map.class);
        config.merge(appConfigMap);
        config.addAttribute("json", appConfig.toString().replace('\"', '\''));
    }
}