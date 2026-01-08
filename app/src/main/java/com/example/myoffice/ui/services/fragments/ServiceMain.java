package com.example.myoffice.ui.services.fragments;

import android.app.Service;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.example.myoffice.Login;
import com.example.myoffice.R;
import com.example.myoffice.databinding.ServiceArchiveBinding;
import com.example.myoffice.databinding.ServiceCodesBinding;
import com.example.myoffice.databinding.ServiceEmployersActionsBinding;
import com.example.myoffice.databinding.ServiceEmployersBinding;
import com.example.myoffice.databinding.ServiceLatesBinding;
import com.example.myoffice.databinding.ServiceMainBinding;
import com.example.myoffice.databinding.ServiceMessagesBinding;
import com.example.myoffice.databinding.ServiceScheduleWorkBinding;
import com.example.myoffice.databinding.ServiceStatementsBinding;
import com.example.myoffice.ui.services.classes.Employer;
import com.example.myoffice.ui.services.classes.ServiceItem;
import com.example.myoffice.ui.services.classes.StatementClass;
import com.example.myoffice.ui.services.services_pages.ServiceActionsEmployers;
import com.example.myoffice.ui.services.services_pages.ServiceArchive;
import com.example.myoffice.ui.services.services_pages.ServiceCodes;
import com.example.myoffice.ui.services.services_pages.ServiceEmployers;
import com.example.myoffice.ui.services.services_pages.ServiceLates;
import com.example.myoffice.ui.services.services_pages.ServiceScheduleWork;
import com.example.myoffice.ui.services.services_pages.ServiceStatements;
import com.example.myoffice.ui.services.services_pages.ServiceMessages;
import com.example.myoffice.user_data;

import java.sql.SQLException;
import java.util.Date;

public class ServiceMain extends Fragment
{
    public ServiceMainBinding binding;
    public ServiceCodesBinding binding_codes;
    public ServiceEmployersBinding binding_employers;
    public ServiceStatementsBinding binding_statements;
    public ServiceEmployersActionsBinding binding_action_employers;
    public ServiceArchiveBinding binding_archive;
    public ServiceMessagesBinding binding_messages;
    public ServiceScheduleWorkBinding binding_schedule_work;
    public ServiceLatesBinding binding_lates;
    public static Fragment fragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.fragment = getParentFragment();

        binding = ServiceMainBinding.inflate(inflater, container, false);
        binding_codes = ServiceCodesBinding.inflate(inflater, container, false);
        binding_employers = ServiceEmployersBinding.inflate(inflater, container, false);
        binding_statements = ServiceStatementsBinding.inflate(inflater, container, false);
        binding_action_employers = ServiceEmployersActionsBinding.inflate(inflater, container, false);
        binding_archive = ServiceArchiveBinding.inflate(inflater, container, false);
        binding_messages = ServiceMessagesBinding.inflate(inflater, container, false);
        binding_schedule_work = ServiceScheduleWorkBinding.inflate(inflater, container, false);
        binding_lates = ServiceLatesBinding.inflate(inflater, container, false);

        View root = binding.getRoot();

        int bundle_service_type = getArguments().getInt("SERVICE_TYPE");
        Services.E_SERVICE_TYPE service_type = ServiceItem.getServiceTypeFromInt(bundle_service_type);

        switch(service_type)
        {
            case SERVICE_TYPE_VACATION:
                root = binding_statements.getRoot();
                ServiceStatements serviceStatements = new ServiceStatements(requireContext());

                try {
                    if(!serviceStatements.LoadStatements(binding_statements))
                        Toast.makeText(requireContext(), "Не получилось загрузить заявления", Toast.LENGTH_SHORT).show();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                break;
            case SERVICE_TYPE_CODES:
                try
                {
                    root = binding_codes.getRoot();
                    ServiceCodes serviceCodes = new ServiceCodes(requireContext());

                    if(!serviceCodes.LoadCodes(binding_codes))
                        Toast.makeText(requireContext(), "Не получилось загрузить ключи доступа", Toast.LENGTH_SHORT).show();
                } catch (SQLException e)
                {
                    throw new RuntimeException(e);
                }
                break;

            case SERVICE_TYPE_EMPLOYERS:
                try
                {
                    root = binding_employers.getRoot();
                    ServiceEmployers serviceEmployers = new ServiceEmployers(requireContext());

                    if(!serviceEmployers.LoadEmployers(binding_employers))
                        Toast.makeText(requireContext(), "Не получилось загрузить страницу", Toast.LENGTH_SHORT).show();
                } catch (SQLException e)
                {
                    throw new RuntimeException(e);
                }
                break;

            case SERVICE_TYPE_ACTIONS_EMPLOYERS:
                try
                {
                    root = binding_action_employers.getRoot();
                    ServiceActionsEmployers serviceEmployers = new ServiceActionsEmployers(requireContext());

                    if(!serviceEmployers.LoadEmployers(binding_action_employers))
                        Toast.makeText(requireContext(), "Не получилось загрузить страницу", Toast.LENGTH_SHORT).show();
                } catch (SQLException e)
                {
                    throw new RuntimeException(e);
                }
                break;

            case SERVICE_TYPE_ARCHIVE:
                try
                {
                    root = binding_archive.getRoot();
                    ServiceArchive serviceArchive = new ServiceArchive(requireContext());

                    if(!serviceArchive.LoadItems(binding_archive))
                        Toast.makeText(requireContext(), "Не получилось загрузить страницу", Toast.LENGTH_SHORT).show();
                } catch (SQLException e)
                {
                    throw new RuntimeException(e);
                }
                break;

            case SERVICE_TYPE_MESSAGES:
                try
                {
                    root = binding_messages.getRoot();
                    ServiceMessages serviceMessages = new ServiceMessages(requireContext());

                    if(!serviceMessages.LoadMessages(binding_messages))
                        Toast.makeText(requireContext(), "Не получилось загрузить страницу", Toast.LENGTH_SHORT).show();
                } catch (SQLException e)
                {
                    throw new RuntimeException(e);
                }
                break;

            case SERVICE_TYPE_SCHEDULE_WORK:
                root = binding_schedule_work.getRoot();
                ServiceScheduleWork serviceScheduleWork = new ServiceScheduleWork(requireContext());

                if(!serviceScheduleWork.LoadCalendar(binding_schedule_work))
                    Toast.makeText(requireContext(), "Не получилось загрузить страницу", Toast.LENGTH_SHORT).show();
                break;

            case SERVICE_TYPE_LATE:
                root = binding_lates.getRoot();
                ServiceLates serviceLates = new ServiceLates(requireContext());

                try {
                    if(!serviceLates.LoadLates(binding_lates))
                        Toast.makeText(requireContext(), "Не получилось загрузить страницу", Toast.LENGTH_SHORT).show();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                break;
        }

        Login.playClickSound(requireContext(), "click_move");

        return root;
    }

    public static void ShowServiceStatementConfirm(int accountid, int statementID, String statement_name, String full_name,
                                                   Date date_start, Date date_end, Date date_create, String comment, String token_notification)
    {
        NavController navController = NavHostFragment.findNavController(fragment);
        Bundle bundle = new Bundle();

        bundle.putInt("SERVICE_ADDITIONAL_TYPE", ServiceAdditional.E_SERVICE_ADDITIONAL_TYPE.SERVICE_ADDITIONAL_TYPE_STATEMENT_CONFIRM.ordinal());
        bundle.putInt("STATEMENT_ID", statementID);
        bundle.putInt("STATEMENT_ACCOUNT_ID", accountid);

        bundle.putString("STATEMENT_NAME", statement_name);
        bundle.putString("STATEMENT_FULL_NAME", full_name);
        bundle.putString("STATEMENT_DATE_START", date_start.toString());
        bundle.putString("STATEMENT_DATE_END", date_end.toString());
        bundle.putString("STATEMENT_DATE_CREATE", date_create.toString());
        bundle.putString("STATEMENT_COMMENT", comment);
        bundle.putString("STATEMENT_TOKEN_NOTIFICATION", token_notification);

        NavOptions navOptions = new NavOptions.Builder()
                .setEnterAnim(R.anim.fade_in)
                .setExitAnim(R.anim.slide_out_left)
                .setPopEnterAnim(R.anim.fade_in)
                .setPopExitAnim(R.anim.fade_out)
                .build();

        navController.navigate(R.id.action_service_main_to_service_additional, bundle, navOptions);
    }

    public static void ShowServiceStatementInput(Employer employer)
    {
        NavController navController = NavHostFragment.findNavController(fragment);
        Bundle bundle = new Bundle();

        bundle.putInt("SERVICE_ADDITIONAL_TYPE", ServiceAdditional.E_SERVICE_ADDITIONAL_TYPE.SERVICE_ADDITIONAL_TYPE_STATEMENT_INPUT.ordinal());
        bundle.putInt("STATEMENT_INPUT_ACCOUNT_ID", employer.getAccountID());

        NavOptions navOptions = new NavOptions.Builder()
                .setEnterAnim(R.anim.fade_in)
                .setExitAnim(R.anim.slide_out_left)
                .setPopEnterAnim(R.anim.fade_in)
                .setPopExitAnim(R.anim.fade_out)
                .build();

        navController.navigate(R.id.action_service_main_to_service_additional, bundle, navOptions);
    }

    public static void ShowServiceMessageInfo(int messageid, int is_read, String title, String message, String send_full_name, Date send_date)
    {
        NavController navController = NavHostFragment.findNavController(fragment);
        Bundle bundle = new Bundle();

        bundle.putInt("SERVICE_ADDITIONAL_TYPE", ServiceAdditional.E_SERVICE_ADDITIONAL_TYPE.SERVICE_ADDITIONAL_TYPE_MESSAGE_INFO.ordinal());
        bundle.putInt("MESSAGE_ID", messageid);
        bundle.putInt("MESSAGE_IS_READ", is_read);

        bundle.putString("MESSAGE_TITLE", title);
        bundle.putString("MESSAGE", message);
        bundle.putString("MESSAGE_SEND_FULL_NAME", send_full_name);
        bundle.putString("MESSAGE_SEND_DATE", send_date.toString());

        NavOptions navOptions = new NavOptions.Builder()
                .setEnterAnim(R.anim.fade_in)
                .setExitAnim(R.anim.slide_out_left)
                .setPopEnterAnim(R.anim.fade_in)
                .setPopExitAnim(R.anim.fade_out)
                .build();

        navController.navigate(R.id.action_service_main_to_service_additional, bundle, navOptions);
    }

    public static void ShowServiceMessageInput()
    {
        NavController navController = NavHostFragment.findNavController(fragment);
        Bundle bundle = new Bundle();

        bundle.putInt("SERVICE_ADDITIONAL_TYPE", ServiceAdditional.E_SERVICE_ADDITIONAL_TYPE.SERVICE_ADDITIONAL_TYPE_MESSAGE_INPUT.ordinal());
        bundle.putInt("MESSAGE_INPUT_ACCOUNT_ID", (int) user_data.GetUserData(user_data.E_USER_DATA.DATA_ID));

        NavOptions navOptions = new NavOptions.Builder()
                .setEnterAnim(R.anim.fade_in)
                .setExitAnim(R.anim.slide_out_left)
                .setPopEnterAnim(R.anim.fade_in)
                .setPopExitAnim(R.anim.fade_out)
                .build();

        navController.navigate(R.id.action_service_main_to_service_additional, bundle, navOptions);
    }

    public static void ShowServiceLateInput()
    {
        NavController navController = NavHostFragment.findNavController(fragment);
        Bundle bundle = new Bundle();

        bundle.putInt("SERVICE_ADDITIONAL_TYPE", ServiceAdditional.E_SERVICE_ADDITIONAL_TYPE.SERVICE_ADDITIONAL_TYPE_LATE_INPUT.ordinal());
        bundle.putInt("LATE_INPUT_ACCOUNT_ID", (int) user_data.GetUserData(user_data.E_USER_DATA.DATA_ID));

        NavOptions navOptions = new NavOptions.Builder()
                .setEnterAnim(R.anim.fade_in)
                .setExitAnim(R.anim.slide_out_left)
                .setPopEnterAnim(R.anim.fade_in)
                .setPopExitAnim(R.anim.fade_out)
                .build();

        navController.navigate(R.id.action_service_main_to_service_additional, bundle, navOptions);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
