/*
 * Copyright (C) 2018 The LineageOS Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.lineageos.settings.dirac;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragment;
import androidx.preference.SwitchPreference;

import org.lineageos.settings.R;

public class DiracSettingsFragment extends PreferenceFragment implements
        Preference.OnPreferenceChangeListener, CompoundButton.OnCheckedChangeListener {

    private static final String PREF_HEADSET = "dirac_headset_pref";
    private static final String PREF_PRESET = "dirac_preset_pref";

    private TextView mTextView;
    private View mSwitchBar;

    private ListPreference mHeadsetType;
    private ListPreference mPreset;

    private DiracUtils mDiracUtils;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.dirac_settings);

        mDiracUtils = new DiracUtils(getContext());

        final ActionBar actionBar = getActivity().getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        boolean enhancerEnabled = mDiracUtils.isDiracEnabled();

        mHeadsetType = (ListPreference) findPreference(PREF_HEADSET);
        mHeadsetType.setOnPreferenceChangeListener(this);
        mHeadsetType.setEnabled(enhancerEnabled);

        mPreset = (ListPreference) findPreference(PREF_PRESET);
        mPreset.setOnPreferenceChangeListener(this);
        mPreset.setEnabled(enhancerEnabled);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.dirac,
                container, false);
        ((ViewGroup) view).addView(super.onCreateView(inflater, container, savedInstanceState));
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        boolean enhancerEnabled = mDiracUtils.isDiracEnabled();

        mTextView = view.findViewById(R.id.switch_text);
        mTextView.setText(getString(enhancerEnabled ?
                R.string.switch_bar_on : R.string.switch_bar_off));

        mSwitchBar = view.findViewById(R.id.switch_bar);
        Switch switchWidget = mSwitchBar.findViewById(android.R.id.switch_widget);
        switchWidget.setChecked(enhancerEnabled);
        switchWidget.setOnCheckedChangeListener(this);
        mSwitchBar.setActivated(enhancerEnabled);
        mSwitchBar.setOnClickListener(v -> {
            switchWidget.setChecked(!switchWidget.isChecked());
            mSwitchBar.setActivated(switchWidget.isChecked());
        });
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        switch (preference.getKey()) {
            case PREF_HEADSET:
                mDiracUtils.setHeadsetType(Integer.parseInt(newValue.toString()));
                return true;
            case PREF_PRESET:
                mDiracUtils.setLevel(String.valueOf(newValue));
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        mDiracUtils.setEnabled(isChecked);

        mTextView.setText(getString(isChecked ? R.string.switch_bar_on : R.string.switch_bar_off));
        mSwitchBar.setActivated(isChecked);

        mHeadsetType.setEnabled(isChecked);
        mPreset.setEnabled(isChecked);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getActivity().onBackPressed();
            return true;
        }
        return false;
    }
}