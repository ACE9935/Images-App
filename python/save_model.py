import tensorflow as tf
import numpy as np
import matplotlib.pyplot as plt
from keras.layers import TFSMLayer

# Load CIFAR-10 dataset
(x_train, y_train), (x_test, y_test) = tf.keras.datasets.cifar10.load_data()
x_test = x_test / 255.0  # Normalize

# Class names
class_names = ['airplane', 'automobile', 'bird', 'cat', 'deer', 
               'dog', 'frog', 'horse', 'ship', 'truck']

# Load model as TFSMLayer (Keras 3)
model = tf.keras.Sequential([
    TFSMLayer('new_cifar10_model', call_endpoint='serving_default')  # Note: endpoint name may vary!
])

# Make prediction (returns dict)
output_dict = model.predict(np.expand_dims(x_test[0], axis=0))

# Extract predictions (adjust key if needed)
if isinstance(output_dict, dict):
    predictions = output_dict['output_0']  # Common key, may need adjustment
else:
    predictions = output_dict  # If not dict

predicted_class = np.argmax(predictions)
confidence = predictions[0][predicted_class]
predicted_label = class_names[predicted_class]
true_label = class_names[y_test[0][0]]

print("\nPrediction Results:")
print(f"True label: {true_label}")
print(f"Predicted: {predicted_label} (confidence: {confidence:.2%})")

# Display image
plt.imshow(x_test[0])
plt.title(f"True: {true_label}\nPredicted: {predicted_label} ({confidence:.2%})")
plt.axis('off')
plt.show()
