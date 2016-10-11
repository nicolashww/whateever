package ruby.keyboardwarrior.data.task;

//import ruby.keyboardwarrior.data.tag.UniqueTagList;

import java.util.Objects;

/**
 * Represents a Person in the address book.
 * Guarantees: details are present and not null, field values are validated.
 */
public class Task implements ReadOnlyTask {

    private TaskDetails details;
    private Phone phone;
    private Email email;
    private Address address;

//    private final UniqueTagList tags;
    /**
     * Assumption: Every field must be present and not null.
     */
    public Task(TaskDetails details, Phone phone, Email email, Address address/*, UniqueTagList tags*/) {
        this.details = details;
        this.phone = phone;
        this.email = email;
        this.address = address;
//        this.tags = new UniqueTagList(tags); // protect internal tags from changes in the arg list
    }

    /**
     * Copy constructor.
     */
    public Task(ReadOnlyTask source) {
        this(source.getDetails(), source.getPhone(), source.getEmail(), source.getAddress()/*, source.getTags()*/);
    }

    @Override
    public TaskDetails getDetails() {
        return details;
    }

    @Override
    public Phone getPhone() {
        return phone;
    }

    @Override
    public Email getEmail() {
        return email;
    }

    @Override
    public Address getAddress() {
        return address;
    }

/*    @Override
    public UniqueTagList getTags() {
        return new UniqueTagList(tags);
    }*/

    /**
     * Replaces this person's tags with the tags in the argument tag list.
     *//*
    public void setTags(UniqueTagList replacement) {
        tags.setTags(replacement);
    }*/

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof ReadOnlyTask // instanceof handles nulls
                && this.isSameStateAs((ReadOnlyTask) other));
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(details, phone, email, address/*, tags*/);
    }

    @Override
    public String toString() {
        return getAsTextShowAll();
    }

}