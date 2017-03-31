package cn.kavel.demo.javantwk.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cn.kavel.demo.javantwk.R;
import cn.kavel.demo.javantwk.interfaces.Experiment;

public class AboutFragment extends Fragment implements Experiment{

    //@Override
    public String getExperimentTitle() {
        return null;
    }

    //@Override
    public String getExperimentDescription() {
        return null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_about, container, false);
    }

}
