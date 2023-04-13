package com.example.menuapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class SurveyAdapter extends BaseAdapter {
    private static String ADDRESS_SURVEY = "http://52.78.72.175/data/preference/update";
    ArrayList<SurveyItem> data = new ArrayList<SurveyItem>();

    PostSurvey postSurvey;
    String token;
    int id, radio;

    public SurveyAdapter(String token){
        this.token = token;
    }
    @Override
    public int getCount(){
        return data.size();
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public SurveyItem getItem(int position) {
        return data.get(position);
    }
    @Override
    public View getView(int position, View view, ViewGroup parent){
        Context context = parent.getContext();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.item_survey, parent, false);

        ImageView imageView = view.findViewById(R.id.img_survey);
        TextView name = view.findViewById(R.id.menu_survey);
        RadioGroup radioGroup = view.findViewById(R.id.radio_group);
        View bodyView = view.findViewById(R.id.survey_body);

        SurveyItem surveyItem = data.get(position);
        name.setText(surveyItem.getName());

        /*bodyView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                id = surveyItem.getId();
            }
        });*/

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.radio_good:
                        radio = 100;
                        break;
                    case R.id.radio_soso:
                        radio = 50;
                        break;
                    case R.id.radio_bad:
                        radio = -1;
                        break;
                }
                /*surveyItem.setPreference(radio);
                id = surveyItem.getId();
                String Id = String.valueOf(id);
                String Radio = String.valueOf(radio);
                postSurvey = new PostSurvey(context);
                PostSurvey.execute(ADDRESS_SURVEY, Id, Radio , token);*/
            }
        });
        return view;
    }
    public void addSItem(int id, String category, String name, String image, int preference){
        SurveyItem item = new SurveyItem();
        item.setId(id);
        item.setCategory(category);
        item.setName(name);
        item.setImage(image);
        item.setPreference(preference);
        data.add(item);
    }
}
