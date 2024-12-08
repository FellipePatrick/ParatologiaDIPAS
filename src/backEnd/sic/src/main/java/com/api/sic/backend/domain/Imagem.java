package com.api.sic.backend.domain;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@SQLDelete(sql = "UPDATE imagem SET deleted_at = CURRENT_TIMESTAMP WHERE id=?")
@SQLRestriction("deleted_at is null")
@Entity
@Table(name = "imagem")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Imagem extends AbstractEntity{
    
    private String nome;
    
    private String codigoIm;

    private String path;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "id_relatorio", referencedColumnName = "id")
    private Relatorio relatorio;    
}
