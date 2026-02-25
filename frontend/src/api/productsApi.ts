import api from "./axios";

export type Product = {
    id: string;
    name: string;
    description?: string | null;
    price: number;
    stock: number;
    createdAt?: string;
    created_at?: string;
};

export type ProductUpsertRequest = {
    name: string;
    description?: string;
    price: number;
    stock: number;
};

export const getProducts = async (_token: string): Promise<Product[]> => {
    const response = await api.get<Product[]>("/products");
    return response.data;
};

export const createProduct = async (
    _token: string,
    payload: ProductUpsertRequest
): Promise<Product> => {
    const response = await api.post<Product>("/products", payload);
    return response.data;
};

export const updateProduct = async (
    _token: string,
    id: string,
    payload: ProductUpsertRequest
): Promise<Product> => {
    const response = await api.put<Product>(`/products/${id}`, payload);
    return response.data;
};

export const deleteProduct = async (_token: string, id: string): Promise<void> => {
    await api.delete(`/products/${id}`);
};
