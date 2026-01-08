package com.example.myoffice.ui.services.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myoffice.R;
import com.example.myoffice.databinding.FragmentServicesBinding;
import com.example.myoffice.ui.adapters.ServiceAdapter;
import com.example.myoffice.ui.services.classes.ServiceItem;
import com.example.myoffice.user_data;

import java.util.ArrayList;
import java.util.List;

public class Services extends Fragment {

    private FragmentServicesBinding binding;
    public enum E_SERVICE_TYPE
    {
        SERVICE_TYPE_VACATION,
        SERVICE_TYPE_CODES,
        SERVICE_TYPE_EMPLOYERS,
        SERVICE_TYPE_ACTIONS_EMPLOYERS,
        SERVICE_TYPE_ARCHIVE,
        SERVICE_TYPE_MESSAGES,
        SERVICE_TYPE_SCHEDULE_WORK,
        SERVICE_TYPE_LATE,
        //
        SERVICE_TYPE_COUNT
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentServicesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        List<ServiceItem> services = new ArrayList<>();
        services.add(new ServiceItem("Заявления", "Подайте заявление на отпуск, свой счёт или больничный", R.drawable.ic_hardcover_foreground, E_SERVICE_TYPE.SERVICE_TYPE_VACATION));
        services.add(new ServiceItem("Сообщения", "Последние сообщения профиля", R.drawable.ic_notifications_foreground, E_SERVICE_TYPE.SERVICE_TYPE_MESSAGES));
        services.add(new ServiceItem("График работы", "Следите за вашими рабочими и выходными днями", R.drawable.ic_schedule_work_foreground, E_SERVICE_TYPE.SERVICE_TYPE_SCHEDULE_WORK));
        services.add(new ServiceItem("Опоздания", "Пусть Ваш начальник знает когда Вы опоздаете", R.drawable.ic_late_foreground, E_SERVICE_TYPE.SERVICE_TYPE_LATE));

        if(user_data.IsAllowRankGiveCodes((Integer) user_data.GetUserData(user_data.E_USER_DATA.DATA_JOB_RANK))) {
            services.add(new ServiceItem("Ключи доступа", "Выдай ключ доступа новому сотруднику", R.drawable.ic_key_foreground, E_SERVICE_TYPE.SERVICE_TYPE_CODES));
            services.add(new ServiceItem("Сотрудники", "Редактируй, добавляй, удаляй сотрудников", R.drawable.ic_employers_foreground, E_SERVICE_TYPE.SERVICE_TYPE_EMPLOYERS));
            services.add(new ServiceItem("Операции", "Поощрения и наказания сотрудников", R.drawable.ic_actions_employees_foreground, E_SERVICE_TYPE.SERVICE_TYPE_ACTIONS_EMPLOYERS));
            services.add(new ServiceItem("Архив", "Архив профилей, заявлений, действий", R.drawable.ic_archive_foreground, E_SERVICE_TYPE.SERVICE_TYPE_ARCHIVE));
        }

        ServiceAdapter adapter;
        adapter = new ServiceAdapter(services, service ->
        {
            NavController navController = NavHostFragment.findNavController(this);
            Bundle bundle = new Bundle();

            bundle.putInt("SERVICE_TYPE", service.GetServiceType());

            NavOptions navOptions = new NavOptions.Builder()
                    .setEnterAnim(R.anim.fade_in)
                    .setExitAnim(R.anim.slide_out_left)
                    .setPopEnterAnim(R.anim.fade_in)
                    .setPopExitAnim(R.anim.fade_out)
                    .build();

            navController.navigate(R.id.action_navigation_services_to_sickLeave, bundle, navOptions);
        });

        recyclerView.setAdapter(adapter);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}