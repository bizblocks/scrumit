package com.company.scrumit.service;

import au.com.bytecode.opencsv.CSVReader;
import com.company.scrumit.entity.City;
import com.company.scrumit.entity.Contact;
import com.company.scrumit.entity.ContactsSpeciality;
import com.company.scrumit.entity.Speciality;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.global.Metadata;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;

@Service(ImportCSVService.NAME)
public class ImportCSVServiceBean implements ImportCSVService {
    public List<HashMap<String, String>> parseCSV(String csv) throws IOException {
        CSVReader reader = new CSVReader(new StringReader(csv), ',', '"');
        List<HashMap<String, String>> res = new LinkedList<>();
        String[] firstLine = reader.readNext();
        String[] line = firstLine;
        while (line != null) {
            line = reader.readNext();
            if (line == null)
                break;
            HashMap<String, String> hash = new HashMap<>();
            for (int i = 0; i < firstLine.length; i++)
                hash.put(firstLine[i], line[i]);
            ((LinkedList<HashMap<String, String>>) res).push(hash);
        }
        return res;
    }

    @Inject
    private Persistence persistence;

    @Inject
    private Metadata metadata;

    @Override
    public void importContacts(List<HashMap<String, String>> data) {
        try(Transaction t = persistence.createTransaction())
        {
            EntityManager em = persistence.getEntityManager();
            List<String> used = Arrays.asList("город", "e-mail", "специализации", "контакты", "имя");
            for (HashMap<String,String> item: data) {
                if(item.get("e-mail")==null)
                    continue;
                Contact contact = metadata.create(Contact.class);
                contact.setFio(item.get("имя"));
                contact.setContacts(item.get("контакты"));
                contact.setCity(City.getCityByName(item.get("город")));
                contact.setEmail(item.get("e-mail"));
                StringBuilder comment = new StringBuilder();
                for(String k:item.keySet())
                {
                    if(used.indexOf(k)>-1)
                        continue;
                    if(item.get(k)==null || "".equals(item.get(k)))
                        continue;
                    comment.append(k).append(": ").append(item.get(k)).append("\n");
                }
                contact.setComments(comment.toString());
                em.persist(contact);

                ContactsSpeciality cs = metadata.create(ContactsSpeciality.class);
                cs.setContact(contact);
                cs.setSpeciality(Speciality.getSpecialityByName(item.get("специализации")));
                em.persist(cs);
            }
            t.commit();
        }
    }

}