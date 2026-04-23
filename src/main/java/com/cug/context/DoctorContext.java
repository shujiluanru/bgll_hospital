package com.cug.context;

public class DoctorContext {
    private static final ThreadLocal<Long> doctorId = new ThreadLocal<>();
    public static void setDoctorId(Long id){
        doctorId.set(id);
    }
    public static Long getDoctorId(){
        return doctorId.get();
    }
    public static void removeDoctorId(){doctorId.remove();
    }

}
