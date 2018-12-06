package com.onval.capstone.repository;

//public class Model {
//
//    private static MutableLiveData<List<Record>> recordsBeingUploaded;
//    private static List<Record> recordList;
//
//    private volatile static Model INSTANCE;
//
//    private Model() {
//        recordList = new ArrayList<>();
//        recordsBeingUploaded = new MutableLiveData<>();
//        recordsBeingUploaded.setValue(recordList);
//    }
//
//    public static Model newInstance() {
//        if (INSTANCE == null) {
//            synchronized (Model.class) {
//                if (INSTANCE == null) {
//                    INSTANCE = new Model();
//                }
//            }
//        }
//
//        return INSTANCE;
//    }
//
//    public LiveData<List<Record>> getRecordsBeingUploaded() {
//        return recordsBeingUploaded;
//    }
//
//    public void addRecording(Record recording) {
////        recordsBeingUploaded.getValue().add(recording);
//        List<Record> temp = recordsBeingUploaded.getValue();
//        temp.add(recording);
//        recordsBeingUploaded.setValue(temp);
//    }
//
//    public void removeRecording(Record recording) {
//        List<Record> temp = recordsBeingUploaded.getValue();
//        temp.remove(recording);
//        recordsBeingUploaded.setValue(temp);
//    }
//}
