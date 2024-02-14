package com.example.productorderservice.product;

import org.springframework.stereotype.Service;

@Service
public class ProductService {
    private final ProductPort productPort;

    public ProductService(final ProductPort productPort) {
        this.productPort = productPort;
    }

    public void addProduct(AddProductRequest request) {
//            throw new UnsupportedOperationException("구현이 안되어 있습니다."); // 테스트 실패 조건
        final Product product = new Product(request.name(), request.price(), request.discountPolicy());
        productPort.save(product);
    }

    public GetProductResponse getProduct(long productId) {
        Product product = productPort.getProduct(productId);

        return new GetProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getDiscountPolicy()
        );
    }

    public void updateProduct(Long productId, UpdateProductRequest request) {
        Product product = productPort.getProduct(productId);
        product.update(request.name(), request.price(),request.discountPolicy());
        productPort.save(product);
    }
}
