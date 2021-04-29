package Models;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.hibernate.annotations.GenericGenerator;
import com.arangodb.entity.DocumentField;
import com.arangodb.entity.DocumentField.Type;

import java.util.*;
import javax.persistence.*;

public class UserLinks {
    private String facebook;
    private String instagram;
    private String twitter;
    private String spotify;
}
