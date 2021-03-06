package ruby.keyboardwarrior.storage.jaxb;

import ruby.keyboardwarrior.data.TasksList;
import ruby.keyboardwarrior.data.exception.IllegalValueException;
import ruby.keyboardwarrior.data.task.TodoTask;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;

/**
 * JAXB-friendly adapted address book data holder class.
 */
@XmlRootElement(name = "KeyboardWarrior")
public class AdaptedTasksList {

    @XmlElement
    private ArrayList<AdaptedTask> tasks = new ArrayList<>();

    /**
     * No-arg constructor for JAXB use.
     */
    public AdaptedTasksList() {}

    /**
     * Converts a given Keyboard Warrior into this class for JAXB use.
     *
     * @param source future changes to this will not affect the created AdaptedAddressBook
     */
    public AdaptedTasksList(TasksList source) {
        tasks = new ArrayList<>();
        for (TodoTask task : source.getAllTasks()) {
            tasks.add(new AdaptedTask(task));
        }
    }


    /**
     * Returns true if any required field is missing.
     *
     * JAXB does not enforce (required = true) without a given XML schema.
     * Since we do most of our validation using the data class constructors, the only extra logic we need
     * is to ensure that every xml element in the document is present. JAXB sets missing elements as null,
     * so we check for that.
     */
    public boolean isAnyRequiredFieldMissing() {
        for (AdaptedTask task : tasks) {
            if (task.isAnyRequiredFieldMissing()) {
                return true;
            }
        }
        return false;
    }


    /**
     * Converts this jaxb-friendly {@code AdaptedTasksList} object into the corresponding(@code TasksList} object.
     * @throws IllegalValueException if there were any data constraints violated in the adapted person
     */
    public TasksList toModelType() throws IllegalValueException {
        final ArrayList<TodoTask> tasksList = new ArrayList<>();
        for (AdaptedTask task : tasks) {
            tasksList.add(task.toModelType());
        }
        return new TasksList(tasksList);
    }
}
