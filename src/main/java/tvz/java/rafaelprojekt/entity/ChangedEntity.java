package tvz.java.rafaelprojekt.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tvz.java.rafaelprojekt.data.ObjectOutputStreamAppender;
import tvz.java.rafaelprojekt.main.MainApplication;


import java.io.*;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ChangedEntity<T> implements Serializable {
    public static Logger logger = LoggerFactory.getLogger(ChangedEntity.class);


    private T oldValue;
    private T newValue;
    private LocalDateTime dateTime;

    public ChangedEntity(T oldValue, T newValue, LocalDateTime dateTime) {
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.dateTime = dateTime;
    }

    public String findChanges() throws IllegalAccessException {

        StringBuffer stringBuffer = new StringBuffer();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if(oldValue != null && newValue !=null)
            {
                Field[] newFields = newValue.getClass().getDeclaredFields();
                Field[] oldFields = oldValue.getClass().getDeclaredFields();
                for(int i=0; i<oldFields.length;i++)
                {
                    oldFields[i].setAccessible(true);
                    newFields[i].setAccessible(true);

                    if(!oldFields[i].get(oldValue).equals(newFields[i].get(newValue)))
                    {
                        stringBuffer.append(oldValue.getClass().getSimpleName() + ": " +"before: " + oldFields[i].getName() + " " + oldFields[i].get(oldValue) + " changed to: " + newFields[i].get(newValue) + " " + dateTime.format(formatter) + " by: " + MainApplication.loginMode + " ");
                    }
                    oldFields[i].setAccessible(false);
                    newFields[i].setAccessible(false);

                }
            }
        else if (oldValue == null && newValue != null){
            Field[] newFields = newValue.getClass().getDeclaredFields();
            stringBuffer.append("Created a new " + newValue.getClass().getSimpleName() + " by " + MainApplication.loginMode + " date: " + dateTime.format(formatter));
            for(int i=0; i< newFields.length;i++)
            {
                newFields[i].setAccessible(true);
                stringBuffer.append(": " + newFields[i].getName() + " "+ newFields[i].get(newValue) );
                newFields[i].setAccessible(false);

            }
        }
        else if(oldValue != null && newValue==null)
        {
            Field[] oldFields = oldValue.getClass().getDeclaredFields();
            stringBuffer.append("Deleted a " + oldValue.getClass().getSimpleName() + " by " + MainApplication.loginMode + " date: " + dateTime.format(formatter));
            for(int i=0; i< oldFields.length;i++) {

                oldFields[i].setAccessible(true);
                stringBuffer.append(": " + oldFields[i].getName() + " " + oldFields[i].get(oldValue));
                oldFields[i].setAccessible(false);
            }
        }

        return stringBuffer.toString();

    }

    public static void serializeChanges(List<String> changedEntityList)
    {
        try {
            FileOutputStream fop=new FileOutputStream("C:\\Users\\Rafael\\IdeaProjects\\Rafael-Projekt\\src\\main\\java\\tvz\\java\\rafaelprojekt\\data\\changes.dat");
            ObjectOutputStream oos=new ObjectOutputStream(fop);
            oos.writeObject(changedEntityList);
            oos.close();
            fop.close();

        }
        catch (IOException e){
            logger.debug(e.getMessage(), e);
            throw new RuntimeException("Error while trying to serialize changes");
        }

    }


    public static List<String> readChanges()
    {
        List<String> list = new ArrayList<>();
        try(FileInputStream fin = new FileInputStream("C:\\Users\\Rafael\\IdeaProjects\\Rafael-Projekt\\src\\main\\java\\tvz\\java\\rafaelprojekt\\data\\changes.dat"))
        {
            ObjectInputStream ois = new ObjectInputStream (fin);
           list = (List<String>)ois.readObject();
           ois.close();
        }
        catch (IOException | ClassNotFoundException e)
        {
            logger.debug(e.getMessage(), e);
            throw new RuntimeException("Error while trying to deserialize changes");
        }

    return list;
    }








}
