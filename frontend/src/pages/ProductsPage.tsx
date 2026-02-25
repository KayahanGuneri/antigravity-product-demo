import React, { useState, useEffect, useCallback } from "react";
import { getToken } from "../auth/tokenStorage";
import { getJwtRole } from "../auth/jwtRole";
import {
    getProducts,
    createProduct,
    updateProduct,
    deleteProduct,
} from "../api/productsApi";
import type { Product, ProductUpsertRequest } from "../api/productsApi";
import type { NormalizedApiError } from "../api/axios";

const ProductsPage: React.FC = () => {
    const [products, setProducts] = useState<Product[]>([]);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string | null>(null);
    const [inFlight, setInFlight] = useState<boolean>(false);

    // Admin form states
    const [showCreate, setShowCreate] = useState<boolean>(false);
    const [newProduct, setNewProduct] = useState<ProductUpsertRequest>({
        name: "",
        description: "",
        price: 0,
        stock: 0,
    });

    // Edit states
    const [editingId, setEditingId] = useState<string | null>(null);
    const [editDraft, setEditDraft] = useState<ProductUpsertRequest>({
        name: "",
        description: "",
        price: 0,
        stock: 0,
    });

    const token = getToken();
    const role = getJwtRole(token);
    const isAdmin = role === "ADMIN";

    const fetchProducts = useCallback(async () => {
        if (!token) return;
        setLoading(true);
        setError(null);
        try {
            const data = await getProducts(token);
            setProducts(data);
        } catch (err: any) {
            const apiError = err as NormalizedApiError;
            setError(apiError.message);
        } finally {
            setLoading(false);
        }
    }, [token]);

    useEffect(() => {
        fetchProducts();
    }, [fetchProducts]);

    const validate = (payload: ProductUpsertRequest) => {
        if (payload.name.trim().length === 0) return "Name is required.";
        if (!isFinite(payload.price) || payload.price <= 0) return "Price must be greater than 0.";
        if (!Number.isInteger(Number(payload.stock)) || payload.stock < 0) return "Stock must be a non-negative integer.";
        return null;
    };

    const handleCreate = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!token) return;

        const validationError = validate(newProduct);
        if (validationError) {
            alert(validationError);
            return;
        }

        setInFlight(true);
        try {
            await createProduct(token, {
                ...newProduct,
                name: newProduct.name.trim(),
                description: newProduct.description?.trim(),
                price: Number(newProduct.price),
                stock: parseInt(String(newProduct.stock), 10),
            });
            setNewProduct({ name: "", description: "", price: 0, stock: 0 });
            setShowCreate(false);
            await fetchProducts();
        } catch (err: any) {
            const apiError = err as NormalizedApiError;
            alert(apiError.message);
        } finally {
            setInFlight(false);
        }
    };

    const handleEditInit = (product: Product) => {
        setEditingId(product.id);
        setEditDraft({
            name: product.name,
            description: product.description || "",
            price: product.price,
            stock: product.stock,
        });
    };

    const handleUpdate = async (id: string) => {
        if (!token) return;

        const validationError = validate(editDraft);
        if (validationError) {
            alert(validationError);
            return;
        }

        setInFlight(true);
        try {
            await updateProduct(token, id, {
                ...editDraft,
                name: editDraft.name.trim(),
                description: editDraft.description?.trim(),
                price: Number(editDraft.price),
                stock: parseInt(String(editDraft.stock), 10),
            });
            setEditingId(null);
            await fetchProducts();
        } catch (err: any) {
            const apiError = err as NormalizedApiError;
            alert(apiError.message);
        } finally {
            setInFlight(false);
        }
    };

    const handleDelete = async (id: string) => {
        if (!token) return;
        if (!window.confirm("Are you sure you want to delete this product?")) return;

        setInFlight(true);
        try {
            await deleteProduct(token, id);
            await fetchProducts();
        } catch (err: any) {
            const apiError = err as NormalizedApiError;
            alert(apiError.message);
        } finally {
            setInFlight(false);
        }
    };

    if (!token) {
        return <div className="products-container"><p>Redirecting to login...</p></div>;
    }

    return (
        <div className="products-container">
            <div className="products-header">
                <h1>Products</h1>
                {isAdmin && (
                    <button
                        className="navbar-btn navbar-btn-primary"
                        onClick={() => setShowCreate(!showCreate)}
                        disabled={inFlight}
                    >
                        {showCreate ? "Cancel" : "Add Product"}
                    </button>
                )}
            </div>

            {isAdmin && showCreate && (
                <form className="product-form" onSubmit={handleCreate}>
                    <h3>Create New Product</h3>
                    <div className="form-group">
                        <label>Name</label>
                        <input
                            type="text"
                            value={newProduct.name}
                            onChange={(e) => setNewProduct({ ...newProduct, name: e.target.value })}
                            required
                        />
                    </div>
                    <div className="form-group">
                        <label>Description</label>
                        <textarea
                            value={newProduct.description}
                            onChange={(e) => setNewProduct({ ...newProduct, description: e.target.value })}
                        />
                    </div>
                    <div className="form-group">
                        <label>Price</label>
                        <input
                            type="number"
                            step="0.01"
                            value={newProduct.price}
                            onChange={(e) => setNewProduct({ ...newProduct, price: Number(e.target.value) })}
                            required
                        />
                    </div>
                    <div className="form-group">
                        <label>Stock</label>
                        <input
                            type="number"
                            value={newProduct.stock}
                            onChange={(e) => setNewProduct({ ...newProduct, stock: parseInt(e.target.value, 10) || 0 })}
                            required
                        />
                    </div>
                    <button type="submit" className="login-btn" disabled={inFlight}>
                        {inFlight ? "Saving..." : "Create Product"}
                    </button>
                </form>
            )}

            {error && <div className="error-message">{error}</div>}

            {loading ? (
                <div className="loading-text">Loading products...</div>
            ) : (
                <table className="products-table">
                    <thead>
                        <tr>
                            <th>Name</th>
                            <th>Description</th>
                            <th>Price</th>
                            <th>Stock</th>
                            {isAdmin && <th>Actions</th>}
                        </tr>
                    </thead>
                    <tbody>
                        {products.map((p) => (
                            <tr key={p.id}>
                                {editingId === p.id ? (
                                    <>
                                        <td>
                                            <input
                                                type="text"
                                                className="form-input"
                                                value={editDraft.name}
                                                onChange={(e) => setEditDraft({ ...editDraft, name: e.target.value })}
                                            />
                                        </td>
                                        <td>
                                            <input
                                                type="text"
                                                className="form-input"
                                                value={editDraft.description}
                                                onChange={(e) => setEditDraft({ ...editDraft, description: e.target.value })}
                                            />
                                        </td>
                                        <td>
                                            <input
                                                type="number"
                                                className="form-input"
                                                step="0.01"
                                                value={editDraft.price}
                                                onChange={(e) => setEditDraft({ ...editDraft, price: Number(e.target.value) })}
                                            />
                                        </td>
                                        <td>
                                            <input
                                                type="number"
                                                className="form-input"
                                                value={editDraft.stock}
                                                onChange={(e) => setEditDraft({ ...editDraft, stock: parseInt(e.target.value, 10) || 0 })}
                                            />
                                        </td>
                                        <td>
                                            <div className="product-actions">
                                                <button className="product-btn-sm" onClick={() => handleUpdate(p.id)} disabled={inFlight}>Save</button>
                                                <button className="product-btn-sm" onClick={() => setEditingId(null)} disabled={inFlight}>Cancel</button>
                                            </div>
                                        </td>
                                    </>
                                ) : (
                                    <>
                                        <td>{p.name}</td>
                                        <td>{p.description || "-"}</td>
                                        <td>${p.price.toFixed(2)}</td>
                                        <td>{p.stock}</td>
                                        {isAdmin && (
                                            <td>
                                                <div className="product-actions">
                                                    <button className="product-btn-sm" onClick={() => handleEditInit(p)} disabled={inFlight}>Edit</button>
                                                    <button className="product-btn-sm product-btn-delete" onClick={() => handleDelete(p.id)} disabled={inFlight}>Delete</button>
                                                </div>
                                            </td>
                                        )}
                                    </>
                                )}
                            </tr>
                        ))}
                    </tbody>
                </table>
            )}

        </div>
    );
};

export default ProductsPage;
