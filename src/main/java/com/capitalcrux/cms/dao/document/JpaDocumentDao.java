package com.capitalcrux.cms.dao.document;

import com.capitalcrux.cms.dao.JpaDao;
import com.capitalcrux.cms.entity.Document;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class JpaDocumentDao extends JpaDao<Document, Long> implements DocumentDao
{
    public JpaDocumentDao()
    {
        super(Document.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Document> findAll()
    {
        final CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
        final CriteriaQuery<Document> criteriaQuery = builder.createQuery(Document.class);

        Root<Document> root = criteriaQuery.from(Document.class);
        criteriaQuery.orderBy(builder.desc(root.get("date")));

        TypedQuery<Document> typedQuery = this.getEntityManager().createQuery(criteriaQuery);
        return typedQuery.getResultList();
    }
}
