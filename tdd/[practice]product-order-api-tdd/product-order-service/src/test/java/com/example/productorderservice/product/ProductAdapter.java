package com.example.productorderservice.product;

import org.springframework.stereotype.Component;

@Component
class ProductAdapter implements ProductPort {

    private final ProductRepository productRepository;

    ProductAdapter(final ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void save(Product product) {
//            throw new UnsupportedOperationException("구현이 안되어있습니다.");
        productRepository.save(product);
    }

    @Override
    public Product getProduct(long productId) {
        return productRepository.findById(productId).orElseThrow(IllegalArgumentException::new);
    }
}
