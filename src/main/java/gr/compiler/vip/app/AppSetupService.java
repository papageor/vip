package gr.compiler.vip.app;

import gr.compiler.vip.entity.IformsSetup;
import gr.compiler.vip.entity.Setup;
import gr.compiler.vip.entity.Vessel;
import io.jmix.core.DataManager;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service("VIP_AppSetupService")
public class AppSetupService {
    @PersistenceContext
    private EntityManager em;

    private static final Logger log = LoggerFactory.getLogger(AppSetupService.class);

    @Inject
    private DataManager dataManager;

    @Transactional
    public Setup GetAppSetup() {
        Setup appSetup = null;

        try {
            TypedQuery<Setup> query = em.createQuery(
                    "select s from VIP_Setup s", Setup.class
            );
            List<Setup> setupList = query.getResultList();
            if (!setupList.isEmpty()) {
                appSetup = setupList.get(0);
                dataManager.load(Setup.class).id(appSetup.getId()).fetchPlan("setup-view").one();
            }

        } catch (Exception e) {
            log.error("Error", e);
        }
        return appSetup;
    }

    public List<Setup> GetAppSetupList() {
        //Setup appSetup = null;

        List<Setup> setupList = null;

        try  {
            TypedQuery<Setup> query = em.createQuery(
                    "select s from VIP_Setup s", Setup.class
            );
            setupList = query.getResultList();

        } catch (Exception e) {
            log.error("Error", e);
        }
        return setupList;
    }

    public IformsSetup GetIformsAppSetup() {
        IformsSetup appSetup = null;

        try {
            TypedQuery<IformsSetup> query = em.createQuery(
                    "select s from VIP_IformsSetup s", IformsSetup.class
            );

            List<IformsSetup> setupList = query.getResultList();
            if (!setupList.isEmpty()) {
                appSetup = setupList.get(0);
            }

        } catch (Exception e) {
            log.error("Error", e);
        }
        return appSetup;
    }

    public boolean isHUB() {
        boolean isHub = true;

        return isHub;
    }

    public boolean isUTCEnabled() {
        boolean isUTCEnabled = false;

        IformsSetup setup = GetIformsAppSetup();
        if (setup != null) {
            isUTCEnabled = setup.getEnableUtc();
        }

        return isUTCEnabled;
    }

    public String getEmailRecipient() {
        String emailRecipient = "";

        IformsSetup setup = GetIformsAppSetup();
        if (setup != null) {
            emailRecipient = setup.getEmailRecipient();
        }

        return emailRecipient;
    }

    public String getLabelForFO() {
        String labelBunker = "";
        IformsSetup setup = GetIformsAppSetup();
        if (setup != null) {
            labelBunker = setup.getLabelFo();
        }

        return labelBunker;
    }

    public String getLabelForGO() {
        String labelBunker = "";
        IformsSetup setup = GetIformsAppSetup();
        if (setup != null) {
            labelBunker = setup.getLabelGo();
        }

        return labelBunker;
    }

    public int getDemoModeOffsetDays() {
        Date demoWorkingDate = new Date();
        int offsetDays = 0;

        try {
            if ((GetAppSetup() != null) &&
                    (GetAppSetup().getDemoMode() != null) &&
                    (GetAppSetup().getDemoMode())) {
                demoWorkingDate = Date.from(GetAppSetup().getDemoWorkingDate().atZone(ZoneId.systemDefault()).toInstant());

                Date startDate = demoWorkingDate;
                Date endDate = new Date();

                long diffInMillies = Math.abs(endDate.getTime() - startDate.getTime());
                long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

                offsetDays = (int) diff;
            }
        } catch (Exception e) {
            offsetDays = 0;

            log.error("Demo Mode offset days cannot be calculated", e);
        }

        return offsetDays;
    }

    public int getDemoModeOffsetDaysByVessel(Vessel vessel) {
        Date demoWorkingDate = new Date();
        int offsetDays = 0;

        try {
            Setup appSetup = GetAppSetupByVessel(vessel);
            if ((appSetup != null) &&
                    (appSetup.getDemoMode() != null) &&
                    (appSetup.getDemoMode())) {
                demoWorkingDate = Date.from(appSetup.getDemoWorkingDate().atZone(ZoneId.systemDefault()).toInstant());

                Date startDate = demoWorkingDate;
                Date endDate = new Date();

                long diffInMillies = Math.abs(endDate.getTime() - startDate.getTime());
                long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

                offsetDays = (int) diff;
            }
        } catch (Exception e) {
            offsetDays = 0;

            log.error("Demo Mode offset days cannot be calculated", e);
        }

        return offsetDays;
    }

    public int getDemoModeOffsetDaysBySID(String sid) {
        Date demoWorkingDate = new Date();
        int offsetDays = 0;

        try {
            Setup appSetup = GetAppSetupBySID(sid);
            if ((appSetup != null) &&
                    (appSetup.getDemoMode() != null) &&
                    (appSetup.getDemoMode())) {
                demoWorkingDate = Date.from(appSetup.getDemoWorkingDate().atZone(ZoneId.systemDefault()).toInstant());

                Date startDate = demoWorkingDate;
                Date endDate = new Date();

                long diffInMillies = Math.abs(endDate.getTime() - startDate.getTime());
                long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

                offsetDays = (int) diff;
            }
        } catch (Exception e) {
            offsetDays = 0;

            log.error("Demo Mode offset days cannot be calculated", e);
        }

        return offsetDays;
    }

    public Setup GetAppSetupByVessel(Vessel vessel) {
        if ((vessel == null) || StringUtils.isBlank(vessel.getTenantId()))
            return GetAppSetup();

        Setup appSetup = null;

        try {
            Optional<Setup> setupOptional = dataManager.load(Setup.class)
                    .query("select s from VIP_Setup s where s.tenant = :tenantId")
                    .parameter("tenantId", vessel.getTenantId())
                    .optional();

            if (setupOptional.isPresent())
                appSetup = setupOptional.get();
            else
                appSetup = GetAppSetup();
        } catch (Exception e) {
            appSetup = null;
            log.error("Cannot find Application Setup", e);
        }

        return appSetup;

    }


    public Setup GetAppSetupBySID(String sid) {
        if (StringUtils.isBlank(sid))
            return GetAppSetup();

        Vessel vessel = null;
        Optional<Vessel> vesselOptional = dataManager.load(Vessel.class)
                .query("select v from VIP_Vessel v where v.referenceId = :referenceId")
                .parameter("referenceId", Integer.parseInt(sid))
                .optional();
        if (vesselOptional.isPresent())
            vessel = vesselOptional.get();

        if ((vessel == null) || StringUtils.isBlank(vessel.getTenantId()))
            return GetAppSetup();

        Setup appSetup = null;

        try {
            Optional<Setup> setupOptional = dataManager.load(Setup.class)
                    .query("select s from VIP_Setup s where s.tenant = :tenantId")
                    .parameter("tenantId", vessel.getTenantId())
                    .optional();

            if (setupOptional.isPresent())
                appSetup = setupOptional.get();
            else
                appSetup = GetAppSetup();
        } catch (Exception e) {
            appSetup = null;
            log.error("Cannot find Application Setup", e);
        }

        return appSetup;
    }

    public LocalDateTime getDefaultStartDateOfOperations(LocalDateTime endDate) {
        LocalDateTime startDate = endDate;

        try {
            Setup setup = GetAppSetup();

            if (setup != null) {
                Integer daysBefore = 7;

                if (setup.getOperationDateRange() != null)
                    daysBefore = setup.getOperationDateRange();

                startDate = endDate.minusDays(daysBefore);
            }
        } catch (Exception e) {
            startDate = endDate;
            log.error("Cannot calculate default Start Date of Vessel's Operation", e);
        }

        return startDate;
    }

    public LocalDateTime getDefaultStartDateOfReports(LocalDateTime endDate) {
        LocalDateTime startDate = endDate;

        try {
            Setup setup = GetAppSetup();

            if (setup != null) {
                Integer daysBefore = 30;

                if (setup.getReportsDateRange() != null)
                    daysBefore = setup.getReportsDateRange();

                startDate = endDate.minusDays(daysBefore);
            }
        } catch (Exception e) {
            startDate = endDate;
            log.error("Cannot calculate default Start Date of Reports", e);
        }

        return startDate;
    }
}