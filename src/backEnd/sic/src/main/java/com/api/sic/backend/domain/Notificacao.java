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
@SQLDelete(sql = "UPDATE notificacao SET deleted_at = CURRENT_TIMESTAMP WHERE id=?")
@SQLRestriction("deleted_at is null")
@Entity
@Table(name = "notificacao")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Notificacao extends AbstractEntity {
    private String titulo;
    private String descricao;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "id_receptor", referencedColumnName = "id")
    private Usuario receptor;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "id_emissor", referencedColumnName = "id")
    private Usuario emissor;
}

