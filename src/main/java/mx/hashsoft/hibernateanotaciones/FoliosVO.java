/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.hashsoft.hibernateanotaciones;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author David
 */

@Entity
@Table(name = "test.folios")
public class FoliosVO implements Serializable {
    
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "foliocompleto")
    private Long folioCompleto;
    
    @Column(name = "SHA1")
    private String sha1;

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the folioCompleto
     */
    public Long getFolioCompleto() {
        return folioCompleto;
    }

    /**
     * @param folioCompleto the folioCompleto to set
     */
    public void setFolioCompleto(Long folioCompleto) {
        this.folioCompleto = folioCompleto;
    }

    /**
     * @return the sha1
     */
    public String getSha1() {
        return sha1;
    }

    /**
     * @param sha1 the sha1 to set
     */
    public void setSha1(String sha1) {
        this.sha1 = sha1;
    }
    
}
