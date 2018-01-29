package com.appsx.childrensactivitycontrol.util;

import android.content.Context;
import android.os.Build;

import com.appsx.childrensactivitycontrol.model.AppListModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class AppListHandler {
    private static final String LOG_TAG = "AppListHandler";
    private final long DAY_ON_MILLISECONDS = 86400000;
    private String[] timePeriodData;
    private String mode;
    private Context context;

    public AppListHandler(String _mode, String[] _timePeriodData, Context _context) {
        this.timePeriodData = _timePeriodData;
        this.mode = _mode;
        this.context = _context;
    }

    public ArrayList<AppListModel> reformatList(ArrayList<AppListModel> inputList) {
        ArrayList<AppListModel> outputList = new ArrayList<>();
        if (!mode.isEmpty()) {
            outputList = inputList;
        } else {
            // Do filter to send data
            String startData = timePeriodData[GlobalNames.START_PERIOD_ID];
            String endData = timePeriodData[GlobalNames.END_PERIOD_ID];

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd / MM / yyyy");
            long filterDateStart = 0;
            long filterDataEnd = 0;

            try {
                Date start = dateFormat.parse(startData);
                filterDateStart = start.getTime();
                Date end = dateFormat.parse(endData);
                filterDataEnd = end.getTime() + DAY_ON_MILLISECONDS;
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // We get the time of the start and end of the event and check them on our range

            for (AppListModel model : inputList) {
                try {
                    long modelDateStart = Long.parseLong(model.getDataStart());
                    long modelDateEnd = Long.parseLong(model.getDataEnd());

                    if (
                            ((modelDateStart < filterDateStart && modelDateEnd > filterDateStart) && (modelDateStart < filterDataEnd && modelDateEnd < filterDataEnd)) ||
                                    ((modelDateStart > filterDateStart && modelDateEnd > filterDateStart) && (modelDateStart < filterDataEnd && modelDateEnd < filterDataEnd)) ||
                                    ((modelDateStart > filterDateStart && modelDateEnd > filterDateStart) && (modelDateStart < filterDataEnd && modelDateEnd > filterDataEnd))
                            ) {
                        outputList.add(model);
                    }
                }catch (NumberFormatException e){
                    e.printStackTrace();
                }

            }
        }

        return mergeEvents(outputList);
    }


    private ArrayList<AppListModel> mergeEvents(ArrayList<AppListModel> inputList) {
        ArrayList<AppListModel> outPutList = new ArrayList<>();
        ArrayList<String> usedApps = new ArrayList<>();
        for (int i = 0; i < inputList.size(); i++) {
            int found = 0;
            String appPackage = inputList.get(i).getAppPackage();

            if (usedApps.size() == 0) {
                usedApps.add(appPackage);
            } else {
                for (int j = 0; j < usedApps.size(); j++) {

                    try {
                        if (usedApps.get(j).equals(appPackage)) {
                            found++;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                if (found == 0) {
                    usedApps.add(appPackage);
                }
            }
        }
        for (String searchAppPackage : usedApps) {
            ArrayList<AppListModel> duplicateInputList = inputList;
            for (int i = 0; i < duplicateInputList.size(); i++) {
                AppListModel verifiableModel = duplicateInputList.get(i);

                if (outPutList.size() == 0) {
                    AppListModel model = verifiableModel;
                    String dateStart = model.getDataStart();
                    String dateEnd = model.getDataEnd();
                    long _dateStart = Long.parseLong(dateStart);
                    long _dataEnd = Long.parseLong(dateEnd);
                    long time = _dataEnd - _dateStart;
                    model.setTime(String.valueOf(time));
                    outPutList.add(model);
                } else {
                    // ищем нужное приложение в списке.
                    if (verifiableModel.getAppPackage().equals(searchAppPackage)) {
                        long appOldTime = 0;
                        boolean matchFound = false;
                        for (int j = 0; j < outPutList.size(); j++) {
                            AppListModel outListAppModel = outPutList.get(j);
                            try {
                                if (outListAppModel.getAppPackage().equals(searchAppPackage)) {

                                    appOldTime = Long.parseLong(outListAppModel.getTime());

                                    long appNewTime = appOldTime +
                                            (Long.parseLong(verifiableModel.getDataEnd())
                                                    - Long.parseLong(verifiableModel.getDataStart()));

                                    outListAppModel.setTime(String.valueOf(appNewTime));

                                    outPutList.set(j, outListAppModel);
                                    matchFound = true;
                                }
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }

                        }
                        if (!matchFound) {
                            AppListModel model = verifiableModel;
                            String dateStart = model.getDataStart();
                            String dateEnd = model.getDataEnd();
                            try {
                                long _dateStart = Long.parseLong(dateStart);
                                long _dataEnd = Long.parseLong(dateEnd);
                                long time = _dataEnd - _dateStart;
                                model.setTime(String.valueOf(time));
                                outPutList.add(model);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }

                        }

                    }

                }
            }
        }
        // Сортируем в порядке убывание модели
        Collections.sort(outPutList,Collections.reverseOrder(AppListModel.COMPARE_BY_TIME));
        return outPutList;
    }


}