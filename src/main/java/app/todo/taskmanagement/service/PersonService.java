package app.todo.taskmanagement.service;

import app.todo.taskmanagement.domain.Person;
import app.todo.taskmanagement.domain.PersonRepository;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class PersonService {
    private final PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public void createPerson(String apellido, String nombre, Integer edad, String dni) {
        var person = new Person();
        person.setApellido(apellido);
        person.setNombre(nombre);
        person.setEdad(edad);
        person.setDni(dni);
        personRepository.saveAndFlush(person);
    }

    public void updatePerson(Person person) {
        personRepository.saveAndFlush(person);
    }

    public void deletePerson(Long id) {
        personRepository.deleteById(id);
    }

    public List<Person> list(Pageable pageable) {
        return personRepository.findAllBy(pageable).toList();
    }

    public Person savePerson(Person person) {
    return personRepository.saveAndFlush(person);
    }

    public List<Person> listAll() {
    return personRepository.findAll();  
    }

    public Person getDefaultPerson() {
    return personRepository.findAll().stream().findFirst().orElse(null);
    }
}
