package be.marche.apptravaux.stock.api

import be.marche.apptravaux.stock.entity.Categorie
import be.marche.apptravaux.stock.entity.Produit

data class StockData(
    val categories: List<Categorie>,
    val produits: List<Produit>
)